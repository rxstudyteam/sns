package com.teamrx.rxtargram.base

import androidx.lifecycle.ViewModel
import com.teamrx.rxtargram.repository.AppDataSource
import io.reactivex.disposables.CompositeDisposable

open class BaseViewModel(protected var dataSource: AppDataSource): ViewModel() {
    protected val disposables = CompositeDisposable()
    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}