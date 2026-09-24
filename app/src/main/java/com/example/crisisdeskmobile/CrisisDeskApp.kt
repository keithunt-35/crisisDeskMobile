package com.example.crisisdeskmobile

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CrisisDeskApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
