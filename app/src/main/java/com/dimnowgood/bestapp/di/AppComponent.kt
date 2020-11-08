package com.dimnowgood.bestapp.di

import com.dimnowgood.bestapp.LiteMailReader
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    ActivityModule::class,
    MailModule::class,
    ViewModelModule::class,
    DatabaseModule::class])
interface AppComponent:AndroidInjector<LiteMailReader>{
    @Component.Factory
    abstract class Factory:AndroidInjector.Factory<LiteMailReader>
}