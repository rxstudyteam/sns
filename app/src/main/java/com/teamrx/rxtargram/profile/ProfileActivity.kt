package com.teamrx.rxtargram.profile

import android.os.Bundle
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity

class ProfileActivity : AppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editor_activity)

        var userId : String? = null
        intent?.extras?.run {
            if(containsKey("userId")){
                userId = getString("userId")
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, Profile.newInstance(userId))
                .commitNow()
        }
    }

}
