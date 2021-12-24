package com.example.multithreaddemo

import android.app.Application
import android.content.Context

/**
 *    Author : wxz
 *    Time   : 2021/12/23
 *    Desc   :
 */
class App:Application() {
    companion object{
        lateinit var context: Context
    }
    override fun onCreate() {
        super.onCreate()
        context=this
    }
}