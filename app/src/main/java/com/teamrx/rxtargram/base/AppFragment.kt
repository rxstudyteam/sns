package com.teamrx.rxtargram.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.teamrx.rxtargram.inject.Injection
import smart.base.BFragment

open class AppFragment : BFragment() {
    protected inline fun <reified T : ViewModel> getViewModel(): T =
            ViewModelProviders.of(this, Injection.provideViewModelFactory()).get(T::class.java)
}