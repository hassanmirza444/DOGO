package com.android_hssn.dogo

import android.app.Application
import android.content.Context
import com.android_hssn.dogo.managers.AdsManager
import com.facebook.stetho.Stetho


class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = this
        Stetho.initializeWithDefaults(this)
        AdsManager.getInstance().initialize(context)
    }

    companion object {
        lateinit var instance: MainApplication
        lateinit var context: Context
    }


}