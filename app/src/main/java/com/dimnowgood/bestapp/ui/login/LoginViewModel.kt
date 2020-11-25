package com.dimnowgood.bestapp.ui.login

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dimnowgood.bestapp.LiteMailReaderApp
import com.dimnowgood.bestapp.di.MailModule
import com.dimnowgood.bestapp.domain.usecase.CheckLoginDataUseCase
import com.dimnowgood.bestapp.util.IS_AUTH
import com.dimnowgood.bestapp.util.NetworkStatus
import com.dimnowgood.bestapp.util.Result
import com.dimnowgood.bestapp.util.Status
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named

class LoginViewModel @ViewModelInject constructor(
    val checkLoginUseCaseUseCase:CheckLoginDataUseCase,
    @MailModule.EncryptSharedPref
    val sharedPref:SharedPreferences,
    val app: Application,
    val networkStatus: NetworkStatus,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val disposables = CompositeDisposable()
    val _status = MutableLiveData<Result<*>>()
    val status: LiveData<Result<*>> = _status

    @SuppressLint("ApplySharedPref")
    fun check(auth: List<String>) {

        _status.value = Result.loading(null, "")

        Single.create<Result<*>>{
            it.onSuccess(checkLoginUseCaseUseCase.checkLog(auth))
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy{ result ->
                if (result.status == Status.SUCCESS) {
                    sharedPref.edit()
                        .putString("login", auth[0])
                        .putString("pass", auth[1])
                        .putBoolean(IS_AUTH, true)
                        .commit()
                }
                _status.value = result
            }.addTo(disposables)
    }

    fun hasConnect() = networkStatus.isConnectNetwork

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}