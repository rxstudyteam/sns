package com.teamrx.rxtargram.base

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.teamrx.rxtargram.inject.Injection
import smart.base.BActivity

@SuppressLint("Registered")
open class AppActivity : BActivity() {
    protected inline fun <reified T : ViewModel> getViewModel(): T = ViewModelProviders.of(this, Injection.provideViewModelFactory()).get(T::class.java)
}