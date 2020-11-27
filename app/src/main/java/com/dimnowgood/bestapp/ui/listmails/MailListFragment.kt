package com.dimnowgood.bestapp.ui.listmails

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dimnowgood.bestapp.R
import com.dimnowgood.bestapp.data.db.MailEntity
import com.dimnowgood.bestapp.databinding.FragmentMailListBinding
import com.dimnowgood.bestapp.util.Status
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@AndroidEntryPoint
class MailListFragment : Fragment() {

    val mailViewModel: MailListViewModel  by viewModels()

    private lateinit var binding: FragmentMailListBinding
    private var commonList = emptyList<MailEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentMailListBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this
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
                true
            }
            R.id.logout ->{
                mailViewModel.backToLoginView()
                    .subscribeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribeBy { result ->
                        if(result){
                            activity?.let{
                                val intent = it.intent
                                it.finish()
                                it.startActivity(intent)
                            }
                        }
                    }
                true
            }
            R.id.item_check -> {
                if(!mailViewModel.hasConnect())
                    Snackbar.make(binding.root,getString(R.string.connect_error),Snackbar.LENGTH_LONG).show()
                else
                    mailViewModel.getNewMails()
                true
            }
            else -> false
        }
    }
}