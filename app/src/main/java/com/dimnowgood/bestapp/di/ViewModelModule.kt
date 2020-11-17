package com.dimnowgood.bestapp.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dimnowgood.bestapp.data.data_source.MailDataSource
import com.dimnowgood.bestapp.ui.ViewModelFactory
import com.dimnowgood.bestapp.data.data_source.YandexMailServerDataSource
import com.dimnowgood.bestapp.ui.listmails.MailListViewModel
import com.dimnowgood.bestapp.ui.login.LoginViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @IntoMap
    @Binds
    @ViewModelKey(LoginViewModel::class)
    fun provideLoginViewModel(LoginViewModel: LoginViewModel): ViewModel

    @IntoMap
    @Binds
    @ViewModelKey(MailListViewModel::class)
    fun provideMailListViewModel(mailListViewModel: MailListViewModel): ViewModel

    @Binds
    fun getMailDataSource(dataSource: YandexMailServerDataSource): MailDataSource

}