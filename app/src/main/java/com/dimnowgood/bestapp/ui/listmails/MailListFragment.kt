package com.dimnowgood.bestapp.ui.listmails

import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.dimnowgood.bestapp.R
import com.dimnowgood.bestapp.data.db.MailEntity
import com.dimnowgood.bestapp.databinding.FragmentMailListBinding
import com.dimnowgood.bestapp.util.Status
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.*
import javax.inject.Inject

class MailListFragment : DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    val mailViewModel: MailListViewModel  by viewModels{
        viewModelFactory
    }

    private lateinit var binding: FragmentMailListBinding
    private var land_orient = false
    private var commonList = emptyList<MailEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentMailListBinding.inflate(inflater,container,false)
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = mailViewModel
        val mailAdapter = MailListAdapter(commonList, R.layout.card_mail){
            mailViewModel.getBodyMail(it)
        }

        land_orient = getResources().configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

//        if(land_orient){
//            viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
//            viewPager?.adapter = mailAdapter
//        }

//       if( == Configuration.ORIENTATION_LANDSCAPE){
//           view.layoutParams = view.layoutParams.apply {
//            height = MATCH_PARENT
//        }
//       }

        binding.recyclerViewMail.apply {
            adapter = mailAdapter
            //addItemDecoration(MailListDivider(ContextCompat.getColor(requireContext(), R.color.backGroundlistItem)))
        }

        mailViewModel.getEmailsUseCase.queryMails().observe(viewLifecycleOwner,{

            commonList = it

            binding.recyclerViewMail.adapter?.apply {
                (this as MailListAdapter).list = commonList
                notifyDataSetChanged()
            }

            binding.viewPager.adapter?.apply {
                (this as MailListAdapter).list = commonList
                notifyDataSetChanged()
            }
        })

        mailViewModel.status.observe(viewLifecycleOwner, {
            when(it.status){
                Status.LOADING -> {}
                Status.ERROR -> {
                    Snackbar.make(view,it.message,Snackbar.LENGTH_LONG).show()
                }
                Status.SUCCESS -> {}
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.mail_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.settings -> {
                findNavController().navigate(R.id.action_mailListFragment_to_settingsFragment)
                true
            }
            R.id.item_check -> {
                mailViewModel.getNewMails()
                true
            }
            else -> false
        }
    }
}

class MailListDivider(colorRes:Int): RecyclerView.ItemDecoration() {

    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorRes
        style = Paint.Style.STROKE
        strokeWidth = 8.toFloat()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        val points = mutableListOf<Float>()
        parent.forEach {
            if (parent.getChildAdapterPosition(it) < state.itemCount - 1) {
                val bottom = (it.bottom + 16).toFloat()
                points.add((it.left).toFloat())
                points.add(bottom)
                points.add(it.right.toFloat())
                points.add(bottom)
            }
        }
        c.drawLines(points.toFloatArray(), dividerPaint)

    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = 0
        outRect.bottom = 0
        outRect.left = 16
        outRect.right = 16
    }

}