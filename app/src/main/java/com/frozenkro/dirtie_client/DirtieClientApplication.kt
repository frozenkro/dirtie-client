package com.frozenkro.dirtie_client

import android.app.Application
import com.frozenkro.dirtie_client.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DirtieClientApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@DirtieClientApplication)
            modules(appModule)
        }
    }
}