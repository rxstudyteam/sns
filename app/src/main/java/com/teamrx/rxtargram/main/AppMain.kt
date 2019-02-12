package com.teamrx.rxtargram.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity
import com.teamrx.rxtargram.editor.EditorActivity
import com.teamrx.rxtargram.detail.DetailViewFragment
import kotlinx.android.synthetic.main.app_main.*

class AppMain : AppActivity() {

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.transaction { replace(R.id.main_content, DetailViewFragment.getInstance()) }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_dashboard -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        main_go_editor_button.setOnClickListener {
            startActivity(Intent(this@AppMain, EditorActivity::class.java))
        }
        navigation.selectedItemId = R.id.navigation_home
    }
}
