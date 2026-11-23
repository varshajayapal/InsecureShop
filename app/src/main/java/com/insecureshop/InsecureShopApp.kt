package com.insecureshop

import android.app.Application
import android.content.Context
import android.util.Log
import com.insecureshop.util.HookingDetector


class InsecureShopApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (HookingDetector.isHooked(this)) {
            throw SecurityException("Unauthorized runtime instrumentation detected")
        }
//        invokePlugins()
    }

    private fun invokePlugins() {

    }


}