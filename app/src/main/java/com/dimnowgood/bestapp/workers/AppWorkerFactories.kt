package com.dimnowgood.bestapp.workers

import android.content.Context
import androidx.work.DelegatingWorkerFactory
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Singleton
import javax.mail.Session

@Singleton
class AppWorkerFactories @Inject constructor(session:Session): DelegatingWorkerFactory() {

    init {
        addFactory(MailWorkerFactory(session))
    }

}

class MailWorkerFactory(val session:Session):WorkerFactory(){
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

       return when(workerClassName){
            CheckNewMailWorker::class.java.name ->
                CheckNewMailWorker(session, appContext, workerParameters)
           else -> null
        }
    }

}