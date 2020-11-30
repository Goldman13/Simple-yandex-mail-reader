package com.dimnowgood.bestapp.ui.listmails

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.dimnowgood.bestapp.data.db.MailEntity
import com.dimnowgood.bestapp.domain.usecase.DeleteMailDbUseCase
import com.dimnowgood.bestapp.domain.usecase.GetMailBodyUseCase
import com.dimnowgood.bestapp.domain.usecase.GetNewEmailUseCase
import com.dimnowgood.bestapp.domain.usecase.ModifyMailItemDbUseCase
import com.dimnowgood.bestapp.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class MailListViewModel @Inject constructor(
    val getEmailsUseCase: GetNewEmailUseCase,
    val getMailBodyUseCase: GetMailBodyUseCase,
    val modifyMailItemDbUseCase: ModifyMailItemDbUseCase,
    val deleteMailDbUseCase: DeleteMailDbUseCase,

    @Named("Encrypt")
    val sharedPref: SharedPreferences,
    val networkStatus: NetworkStatus
) : ViewModel() {

    private val login = sharedPref.getString(LOGIN,"")?:""

    val _status = MutableLiveData<Result<*>>()
    val status: LiveData<Result<*>> = _status

    val mailListDb = MediatorLiveData<List<MailEntity>>()

    init{
        mailListDb.addSource(getEmailsUseCase.queryMails(login)){
            _status.value = Result.success(null, "")
            mailListDb.value = it
        }
    }

    suspend fun getBodyMail(id: Long):String{
        return viewModelScope.async(Dispatchers.IO){
            var body = getMailBodyUseCase.queryBodyDb(id, login)?.content?:""
            if(body.isEmpty()){
                val result = getMailBodyUseCase.loadMailBody(id, login)
                body =
                    if(result.status == Status.SUCCESS)
                        getMailBodyUseCase.queryBodyDb(id, login)?.content?:""
                    else result.message
            }
            body
        }.await()
    }

    suspend fun delete(list: List<MailEntity>){
        viewModelScope.async{
            deleteMailDbUseCase.delete(list)
        }.await()
    }

    suspend fun updateMailDb(mailItem: MailEntity){
        viewModelScope.async {
            modifyMailItemDbUseCase.updateDb(mailItem)
        }.await()
    }

    @SuppressLint("ApplySharedPref")
    suspend fun backToLoginView(){
        viewModelScope.async {
            sharedPref.edit()
                .putString(LOGIN, "")
                .putString(PASSWORD, "")
                .putBoolean(IS_AUTH, false)
                .commit()
        }.await()
    }

    fun getNewMails() {
        _status.value = Result.loading(null, "")
        viewModelScope.launch {
            _status.value = withContext(Dispatchers.IO){getEmailsUseCase.query(login)}
        }
    }

    fun hasConnect() = networkStatus.isConnectNetwork
}