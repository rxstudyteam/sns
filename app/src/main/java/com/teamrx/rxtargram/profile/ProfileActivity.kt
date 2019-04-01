package com.teamrx.rxtargram.profile

import android.os.Bundle
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity

class ProfileActivity : AppActivity() {

    companion object {
        val EXTRA_USER_ID = "user_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editor_activity)

//         : String? = null
//        intent?.extras?.run {
//            if(containsKey("userID")){
//                user_id = getString("userID")
//            }
//        }

        var user_id = intent?.getStringExtra(EXTRA_USER_ID  )

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, Profile.newInstance(user_id))
                .commitNow()
        }
    }

}
