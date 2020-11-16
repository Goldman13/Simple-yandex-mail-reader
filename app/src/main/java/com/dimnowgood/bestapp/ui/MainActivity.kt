package com.dimnowgood.bestapp.ui

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dimnowgood.bestapp.R
import com.dimnowgood.bestapp.databinding.ActivityMainBinding
import com.dimnowgood.bestapp.util.IS_AUTH
import com.dimnowgood.bestapp.workers.CheckNewMailWorker
import com.google.android.material.appbar.CollapsingToolbarLayout
import dagger.android.DaggerActivity
import dagger.android.support.DaggerAppCompatActivity
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    @Named("Encrypt")
    lateinit var sharedPref: SharedPreferences
    private val workMailTag = "com.dimnowgood.bestapp.workMail"
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAppBarVisible(true)
        settingNav()
    }

    fun setAppBarVisible(v: Boolean){
        if(v){
            setSupportActionBar(binding.toolbarMain)
        }else{
            supportActionBar?.hide()
        }
    }

    private fun settingNav(){

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.loginFragment, R.id.mailListFragment))
        binding.collapsingToolbarLayout.setupWithNavController(binding.toolbarMain, navController, appBarConfiguration)

        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.nav_host)

        val isLogged = sharedPref.getBoolean(IS_AUTH, false)

        val destination =
            if(isLogged)
                R.id.mailListFragment
            else
                R.id.loginFragment

        navGraph.startDestination = destination
        navController.graph = navGraph
    }

    override fun onResume() {
        super.onResume()
        WorkManager.getInstance(applicationContext).cancelAllWorkByTag(workMailTag)
    }

    override fun onPause() {
        super.onPause()
        if(!isFinishing){
            runWorker()
        }
    }

    fun runWorker(){

//        val workRequest = OneTimeWorkRequestBuilder<CheckNewMailWorker>()
//            .addTag(workMailTag)
//            .build()
//
//        WorkManager
//            .getInstance(applicationContext)
//            .enqueue(workRequest)


        val workRequest = PeriodicWorkRequestBuilder<CheckNewMailWorker>(
            15,
            TimeUnit.MINUTES)
            .addTag(workMailTag)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            workMailTag,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}