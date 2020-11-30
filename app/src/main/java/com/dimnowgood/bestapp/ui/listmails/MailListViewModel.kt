package com.dimnowgood.bestapp.ui.listmails

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dimnowgood.bestapp.data.db.MailEntity
import com.dimnowgood.bestapp.domain.usecase.DeleteMailDbUseCase
import com.dimnowgood.bestapp.domain.usecase.GetMailBodyUseCase
import com.dimnowgood.bestapp.domain.usecase.GetNewEmailUseCase
import com.dimnowgood.bestapp.domain.usecase.ModifyMailItemDbUseCase
import com.dimnowgood.bestapp.util.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
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

    private val disposable = CompositeDisposable()
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

    fun getBodyMail(id: Long): Single<String>{
        return Single.create{
            var body = getMailBodyUseCase . queryBodyDb (id, login)?.content?:""
            if(body.isEmpty()){
                val result = getMailBodyUseCase.loadMailBody(id, login)
                body =
                    if(result.status == Status.SUCCESS)
                        getMailBodyUseCase.queryBodyDb(id, login)?.content?:""
                    else result.message
            }

            it.onSuccess(body)
        }
    }

    fun delete(list: List<MailEntity>): Completable{
        return Completable.create{
            deleteMailDbUseCase.delete(list)
            it.onComplete()
        }
    }

    fun updateMailDb(mailItem: MailEntity): Completable{
        return Completable.create{
            modifyMailItemDbUseCase.updateDb(mailItem)
            it.onComplete()
        }
    }

    @SuppressLint("ApplySharedPref")
    fun backToLoginView(): Single<Boolean>{
        return Single.create{
            sharedPref.edit()
            .putString(LOGIN, "")
            .putString(PASSWORD, "")
            .putBoolean(IS_AUTH, false)
            .commit()}
    }

    fun getNewMails() {
        _status.value = Result.loading(null, "")
        Single.create<Result<*>>{
            getEmailsUseCase.query(login)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {result ->
                _status.value = result
            }.addTo(disposable)
    }

    fun hasConnect() = networkStatus.isConnectNetwork

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}