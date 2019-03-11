package com.teamrx.rxtargram.editor

import android.os.Bundle
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity
import io.reactivex.Observable

class EditorActivity : AppActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editor_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, EditorFragment.newInstance()).commitNow()
        }
    }

}
