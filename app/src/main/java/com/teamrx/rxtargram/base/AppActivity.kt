package com.teamrx.rxtargram.base

import android.widget.Toast
import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import com.teamrx.rxtargram.inject.Injection
import smart.base.BActivity

@SuppressLint("Registered")
open class AppActivity : BActivity() {

    protected inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

    fun toast(msg: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, msg, duration).show()
    }
}