package com.dimnowgood.bestapp.ui.listmails

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import android.view.*
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.dimnowgood.bestapp.R
import com.dimnowgood.bestapp.data.db.MailEntity
import com.dimnowgood.bestapp.databinding.CardMailBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.card_mail.view.*
import java.text.SimpleDateFormat


class MailListAdapter(
    var list:List<MailEntity>,
    private val appContext: Context,
    val query: (Long) -> Single<String>,
    val update: (MailEntity) -> Completable,
    val delete: (List<MailEntity>) -> Completable
):RecyclerView.Adapter<MailListAdapter.MailListViewHolder>(){

    private  var listDel = mutableSetOf<MailEntity>()
    private var actionMode: ActionMode? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MailListViewHolder {
        return MailListViewHolder(
            CardMailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: MailListViewHolder, position: Int) {
        val item = list.get(position)
        holder.date.text = SimpleDateFormat("yyyy-M-d H:mm").format(item.data_receiving)
        holder.from.text = item.from
        holder.title.text = item.title

        holder.checkItem.visibility =
            if(listDel.contains(item)) View.VISIBLE
            else View.INVISIBLE

        val color =
            if(item.newMail)
                ContextCompat.getColor(appContext, R.color.backGroundlistItem_new)
            else
                ContextCompat.getColor(appContext, R.color.backGroundlistItem)

        holder.itemView.setBackgroundColor(color)

        holder.buttonImage.setOnClickListener {viewButton ->
            holder.webContent.apply {
                if(isVisible){
                    visibility = View.GONE
                    (viewButton as ImageButton).apply {
                        setImageResource(R.drawable.close_cardview_bottom_24)
                    }
                    return@setOnClickListener
                }
            }

            query(item.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {result ->
                    holder.webContent.apply {
                        val encodedHtml = Base64.encodeToString(result.toByteArray(), Base64.NO_PADDING)
                        loadData(encodedHtml, "text/html", "base64")
                        (viewButton as ImageButton).apply {
                            setImageResource(R.drawable.open_cardview_bottom_24)
                        }
                        visibility = View.VISIBLE
                    }
                }
        }
    }

    override fun getItemCount(): Int = list.size

    inner class MailListViewHolder(
        view: View
    ):RecyclerView.ViewHolder(view),View.OnClickListener,View.OnLongClickListener{

        val binding = CardMailBinding.bind(view)

        init{
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        val checkItem = binding.checkItem
        val date = binding.dateReceive
        val from = binding.from
        val title = binding.mailTitle
        val buttonImage = binding.buttonExpand
        val webContent = binding.webView.apply {
            settings.apply {
                useWideViewPort = true
                loadWithOverviewMode = true
            }
        }

        override fun onClick(v: View?) {
            val item = list.get(this.adapterPosition)
            if(actionMode!=null) {
                if(listDel.contains(item)){
                    listDel.remove(item)
                    v?.checkItem?.visibility = View.INVISIBLE
                }
                else{
                    listDel.add(item)
                    v?.checkItem?.visibility = View.VISIBLE
                }
                actionMode?.title = if (listDel.size == 0) "" else listDel.size.toString()
            } else {
                if (item.newMail) {
                    item.newMail = false
                    update(item)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeBy {
                            v?.setBackgroundColor(
                                ContextCompat.getColor(appContext, R.color.backGroundlistItem)
                            )
                        }
                }
            }
        }

        private val actionModeCallback = object : ActionMode.Callback {

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                val inflater: MenuInflater = mode.menuInflater
                inflater.inflate(R.menu.context_menu, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.delItem -> {
                        delete(listDel.toList())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeBy{
                                mode.finish()
                            }
                        true
                    }
                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                actionMode = null
                listDel.clear()
                this@MailListAdapter.notifyDataSetChanged()
            }
        }

        override fun onLongClick(v: View?): Boolean {
            when (actionMode) {
                null -> {
                    actionMode = v?.startActionMode(actionModeCallback)
                }
            }
            return true
        }
    }
}




