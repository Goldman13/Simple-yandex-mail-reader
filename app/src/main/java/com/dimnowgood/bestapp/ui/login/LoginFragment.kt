package com.dimnowgood.bestapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
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
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.viewModel = loginViewModel

        binding.buttonEnter.setOnClickListener {
            val hasError = isEmptyData()
            if(!loginViewModel.hasConnect()){
                Snackbar.make(it,getString(R.string.connect_error),Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

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
                    Snackbar.make(binding.root,it.message,Snackbar.LENGTH_LONG).show()
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