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
import com.dimnowgood.bestapp.ui.MainActivity
import com.dimnowgood.bestapp.util.Status
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class MailListFragment : DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    val mailViewModel: MailListViewModel  by viewModels{
        viewModelFactory
    }

    private lateinit var binding: FragmentMailListBinding
    private var commonList = emptyList<MailEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentMailListBinding.inflate(inflater,container,false)
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.viewModel = mailViewModel
        val mailAdapter = MailListAdapter(
            commonList,
            requireContext().applicationContext,
            {mailViewModel.getBodyMail(it)},
            {mailViewModel.updateMailDb(it)},
            {mailViewModel.delete(it)}
        )


        binding.recyclerViewMail.apply {
            adapter = mailAdapter
        }

        mailViewModel.mailListDb.observe(viewLifecycleOwner,{

            commonList = it

            binding.recyclerViewMail.adapter?.apply {
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
                Status.SUCCESS -> {
                    if(it.message.isNotEmpty())
                        Snackbar.make(view,it.message,Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.mail_list_menu, menu)
        inflater.inflate(R.menu.common_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.settings -> {
                findNavController().navigate(R.id.action_mailListFragment_to_settingsFragment)
                (activity as MainActivity).runWorker()
                true
            }
            R.id.logout ->{
                CoroutineScope(Dispatchers.Default).launch{
                    mailViewModel.backToLoginView()
                    activity?.let{
                        val intent = it.intent
                        it.finish()
                        it.startActivity(intent)
                    }
                }
                true
            }
            R.id.item_check -> {
                if(!mailViewModel.hasConnect())
                    Snackbar.make(binding.root,getString(R.string.connect_error),Snackbar.LENGTH_LONG).show()
                mailViewModel.getNewMails()
                true
            }
            else -> false
        }
    }
}