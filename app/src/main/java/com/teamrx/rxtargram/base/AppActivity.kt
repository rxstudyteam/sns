package com.teamrx.rxtargram.base

import androidx.lifecycle.ViewModelProviders
import com.teamrx.rxtargram.inject.Injection
import smart.base.BActivity

open class AppActivity : BActivity() {

    protected inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }
}