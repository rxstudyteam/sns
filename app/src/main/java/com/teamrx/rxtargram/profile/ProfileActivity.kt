package com.teamrx.rxtargram.profile

import android.os.Bundle
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity

class ProfileActivity : AppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editor_activity)

        var user_id : String? = null
        intent?.extras?.run {
            if(containsKey("userID")){
                user_id = getString("userID")
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, Profile.newInstance(user_id))
                .commitNow()
        }
    }

}
