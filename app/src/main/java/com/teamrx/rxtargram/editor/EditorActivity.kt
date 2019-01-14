package com.teamrx.rxtargram.editor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teamrx.rxtargram.R

class EditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editor_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, EditorFragment.newInstance()).commitNow()
        }
    }

}
