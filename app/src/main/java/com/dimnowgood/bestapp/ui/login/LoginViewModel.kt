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

    val _status = MutableLiveData<Result<*>>()
    val status: LiveData<Result<*>> = _status

    @SuppressLint("ApplySharedPref")
    fun check(auth: List<String>) {
        viewModelScope.launch {
            _status.value = Result.loading(null, "")

            val result = withContext(Dispatchers.IO) {
                checkLoginUseCaseUseCase.checkLog(auth)
            }

            if (result.status == Status.SUCCESS) {
                sharedPref.edit()
                    .putString("login", auth[0])
                    .putString("pass", auth[1])
                    .putBoolean(IS_AUTH, true)
                    .commit()
            }
            _status.value = result
        }
    }

    fun hasConnect() = networkStatus.isConnectNetwork
}