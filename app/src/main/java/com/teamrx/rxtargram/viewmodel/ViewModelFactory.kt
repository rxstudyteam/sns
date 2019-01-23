package com.teamrx.rxtargram.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teamrx.rxtargram.detail.DetailViewModel
import com.teamrx.rxtargram.repository.AppDataSource
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val dataSource: AppDataSource): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(viewModelClass: Class<T>): T {
        return when(viewModelClass) {
            DetailViewModel::class.java -> DetailViewModel(dataSource) as T

            else -> throw IllegalArgumentException("unknown viewmodel class")
        }
    }
}