package com.dalvinlabs.nycschools

import android.app.Application
import com.dalvinlabs.nycschools.di.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class MyApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any?>

    override fun androidInjector(): DispatchingAndroidInjector<Any?> = dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent
            .builder()
            .bindsContext(this.applicationContext)
            .build()
            .inject(this)
    }
}