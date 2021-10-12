package proto.android.stormy

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class Stormy : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: Stormy
    }

    override fun onCreate() {
        super.onCreate()

         instance = this
    }
}