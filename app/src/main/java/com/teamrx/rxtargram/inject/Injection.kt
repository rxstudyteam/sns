package com.teamrx.rxtargram.inject

import com.teamrx.rxtargram.repository.AppDataSource
import com.teamrx.rxtargram.repository.AppRepository
import com.teamrx.rxtargram.repository.RemoteAppDataSource
import com.teamrx.rxtargram.viewmodel.ViewModelFactory

object Injection {
    fun provideAppDataSource(): AppDataSource {
        return AppRepository.getInstance(RemoteAppDataSource)
    }

    fun provideViewModelFactory(): ViewModelFactory {
        val dataSource = provideAppDataSource()
        return ViewModelFactory(dataSource)
    }
}