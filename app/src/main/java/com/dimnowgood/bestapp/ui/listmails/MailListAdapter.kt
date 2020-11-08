package com.dimnowgood.bestapp.ui.listmails

import android.annotation.SuppressLint
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.dimnowgood.bestapp.R
import com.dimnowgood.bestapp.data.db.MailEntity
import kotlinx.coroutines.*
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.zone.ZoneOffsetTransitionRule
import java.util.*
import kotlin.coroutines.CoroutineContext


class MailListAdapter(
    var list:List<MailEntity>,
    private val layout:Int,
    val query: suspend (Long) -> String)
:RecyclerView.Adapter<MailListViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MailListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layout, parent, false)

        return MailListViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: MailListViewHolder, position: Int) {
        val item = list.get(position)
        holder.date.text = SimpleDateFormat("yyyy-M-d H:mm").format(item.data_receiving)
        holder.from.text = item.from
        holder.title.text = item.title

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

            Timber.tag("Yandex").d("1")
            CoroutineScope(Dispatchers.Main).launch{
                Timber.tag("Yandex").d("2")
               //val result = query(item.id).await()
                val result = query(item.id)
                Timber.tag("Yandex").d("5555")

                Timber.tag("Yandex").d("3")

                holder.webContent.apply {
                    val encodedHtml = Base64.encodeToString("JJJJJJJJJJJJJ".toByteArray(), Base64.NO_PADDING)
//                        val encodedHtml = Base64.encodeToString(result.toByteArray(), Base64.NO_PADDING)
                        loadData(encodedHtml, "text/html", "base64")
                        (viewButton as ImageButton).apply {
                            setImageResource(R.drawable.open_cardview_bottom_24)
                        }
                        visibility = View.VISIBLE
                }
            }

            Timber.tag("Yandex").d("4")
        }


    }
    override fun getItemCount(): Int = list.size
}

class MailListViewHolder(view: View):RecyclerView.ViewHolder(view){
    val date = view.findViewById<TextView>(R.id.date_receive)
    val from = view.findViewById<TextView>(R.id.from)
    val title = view.findViewById<TextView>(R.id.mail_title)
    val webContent = view.findViewById<WebView>(R.id.webView).apply {
        settings.apply {
            useWideViewPort = true
            loadWithOverviewMode = true
        }
    }

    val buttonImage = view.findViewById<ImageButton>(R.id.button_expand)
//    val buttonImage = view.findViewById<ImageButton>(R.id.button_expand).setOnClickListener {
//
//        webContent.apply {
//            if(isVisible) visibility = View.GONE
//            else visibility = View.VISIBLE
//        }
//    }
}