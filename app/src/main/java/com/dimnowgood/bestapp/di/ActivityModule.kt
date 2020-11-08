package com.dimnowgood.bestapp.di

import com.dimnowgood.bestapp.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @PerActivity
    @ContributesAndroidInjector(modules = [FragmentsModule::class])
    abstract fun contributeMainActivity():MainActivity
}