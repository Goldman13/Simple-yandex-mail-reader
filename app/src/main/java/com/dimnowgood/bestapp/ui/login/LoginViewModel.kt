package com.dimnowgood.bestapp.ui.login

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.dimnowgood.bestapp.LiteMailReaderApp
import com.dimnowgood.bestapp.domain.usecase.CheckLoginDataUseCase
import com.dimnowgood.bestapp.util.IS_AUTH
import com.dimnowgood.bestapp.util.NetworkStatus
import com.dimnowgood.bestapp.util.Result
import com.dimnowgood.bestapp.util.Status
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.Singles
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class LoginViewModel @Inject constructor(
    val checkLoginUseCaseUseCase:CheckLoginDataUseCase,
    @Named("Encrypt")
    val sharedPref:SharedPreferences,
    val app: LiteMailReaderApp,
    val networkStatus: NetworkStatus
) : ViewModel() {

    private val disposables = CompositeDisposable()
    val _status = MutableLiveData<Result<*>>()
    val status: LiveData<Result<*>> = _status

    @SuppressLint("ApplySharedPref")
    fun check(auth: List<String>) {
        _status.value = Result.loading(null, "")
        Single.create<Result<*>>{checkLoginUseCaseUseCase.checkLog(auth)}
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { result ->
                    if(result.status==Status.SUCCESS){
                        sharedPref.edit()
                            .putString("login", auth[0])
                            .putString("pass", auth[1])
                            .putBoolean(IS_AUTH, true)
                            .commit()
                    }
                    _status.value = result
                }
            ).addTo(disposables)
    }

    fun hasConnect() = networkStatus.isConnectNetwork

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}