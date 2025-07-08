package com.example.hamrothrift

import android.app.Application
import com.google.firebase.FirebaseApp

class HamroThriftApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}