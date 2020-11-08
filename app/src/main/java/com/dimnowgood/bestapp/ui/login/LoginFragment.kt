package com.dimnowgood.bestapp.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dimnowgood.bestapp.R
import com.dimnowgood.bestapp.databinding.FragmentLoginBinding
import com.dimnowgood.bestapp.util.Status
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class LoginFragment:DaggerFragment() {

    lateinit var binding:FragmentLoginBinding
    @Inject lateinit var viewModelFactory:  ViewModelProvider.Factory

    val loginViewModel: LoginViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.hide()
        setHasOptionsMenu(true)
        binding = FragmentLoginBinding.inflate(inflater, container,false)
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //(activity as AppCompatActivity).supportActionBar?.hide()
        binding.viewModel = loginViewModel

        binding.buttonEnter.setOnClickListener {
            val hasError = isEmptyData()
            if(!hasError){
                loginViewModel.check(mutableListOf(
                    binding.textLogin.text.toString(),
                    binding.textPass.text.toString())
                )
            }
        }

        loginViewModel.status.observe(viewLifecycleOwner, {
            when(it.status){
                Status.LOADING -> {}
                Status.ERROR -> {
                    Snackbar.make(view,it.message,Snackbar.LENGTH_LONG).show()
                }
                Status.SUCCESS -> {
                    navigateToMailList()
                }
            }
        })
    }

    private fun navigateToMailList(){
       activity?.let{
         val intent = it.intent
           it.finish()
           it.startActivity(intent)
       }
        //findNavController().navigate(R.id.action_loginFragment_to_mailListFragment)
    }

    private fun isEmptyData():Boolean{

        var isEmpty = false

        with(binding.textLogin){
            if(text.isNullOrEmpty()){
                error = "Empty string"
                isEmpty = true
            }
        }

        with(binding.textPass){
            if(text.isNullOrEmpty()){
                error = "Empty string"
                isEmpty = true
            }
        }
        return isEmpty
    }
}