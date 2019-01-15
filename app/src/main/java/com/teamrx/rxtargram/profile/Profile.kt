package com.teamrx.rxtargram.profile

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity
import com.teamrx.rxtargram.databinding.ProfileWriteBinding

class Profile : AppActivity() {
    private lateinit var bb: ProfileWriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bb = DataBindingUtil.setContentView(this, R.layout.profile_write)
        bb.vm = ViewModelProviders.of(mActivity).get(ProfileViewModel::class.java)
//        bb.model = bb.vm.getProfle()
    }
}