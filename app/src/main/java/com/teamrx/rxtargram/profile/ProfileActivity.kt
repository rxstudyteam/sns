package com.teamrx.rxtargram.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity

class ProfileActivity : AppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editor_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, Fragment.instantiate(this@ProfileActivity, Profile::class.java.name))
                .commitNow()
        }
    }

}
