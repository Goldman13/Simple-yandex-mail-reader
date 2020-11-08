package com.dimnowgood.bestapp.di

import com.dimnowgood.bestapp.ui.login.LoginFragment
import com.dimnowgood.bestapp.ui.listmails.MailListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentsModule {
    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeMailListFragment(): MailListFragment
}