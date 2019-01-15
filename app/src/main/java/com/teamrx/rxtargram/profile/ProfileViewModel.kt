package com.teamrx.rxtargram.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    fun getProfle() {
        ProfileModel("aa", "aa", 3, 3, 3)
    }

}