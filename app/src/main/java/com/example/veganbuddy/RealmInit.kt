package com.example.veganbuddy

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class RealmInit:Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}