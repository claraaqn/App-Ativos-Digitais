package com.projeto1.desingbrabo

import android.app.Application
import com.projeto1.desingbrabo.data.AppDatabase

class MyApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this@MyApplication) }
}