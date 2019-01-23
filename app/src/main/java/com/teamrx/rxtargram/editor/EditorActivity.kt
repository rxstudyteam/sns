package com.teamrx.rxtargram.editor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.teamrx.rxtargram.R
import kotlinx.android.synthetic.main.editor_activity.*

class EditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editor_activity)
        setSupportActionBar(toolbar)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, EditorFragment.newInstance()).commitNow()
        }
    }

}
