package com.dimnowgood.bestapp.ui.listmails

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimnowgood.bestapp.data.db.MailBodyEntity
import com.dimnowgood.bestapp.domain.usecase.GetNewEmailUseCase
import com.dimnowgood.bestapp.util.Result
import com.dimnowgood.bestapp.util.Status
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.system.measureTimeMillis

class MailListViewModel @Inject constructor(
    val getEmailsUseCase: GetNewEmailUseCase,
    @Named("Encrypt")
    val sharedPref: SharedPreferences
) : ViewModel() {

    val _status = MutableLiveData<Result<*>>()
    val status: LiveData<Result<*>> = _status

    suspend fun getBodyMail(id: Long):String{
       return viewModelScope.async(Dispatchers.IO){
              var body = getEmailsUseCase.queryBody(id)?.content?:""
              if(body.isEmpty()){
                if(getEmailsUseCase.loadMailBody(id).status == Status.SUCCESS){
                    body = getEmailsUseCase.queryBody(id)?.content?:""
                }
              }
            body
          }.await()
    }

    fun getNewMails() {
        _status.value = Result.loading("", null)
        viewModelScope.launch() {
            _status.value = withContext(Dispatchers.IO){getEmailsUseCase.query()}
        }
    }
}