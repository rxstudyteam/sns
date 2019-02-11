package smart.base

import android.base.CApplication

open class BApplication : CApplication() {
    override fun onCreate() {
        super.onCreate()
        PP.CREATE(applicationContext)
    }
}