package com.teamrx.rxtargram.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity
import com.teamrx.rxtargram.detail.DetailViewFragment
import com.teamrx.rxtargram.profile.Profile
import kotlinx.android.synthetic.main.app_main.*

class AppMain : AppActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        return@OnNavigationItemSelectedListener when (item.itemId) {
            R.id.navigation_home ->
                supportFragmentManager.transaction { replace(R.id.main_content, DetailViewFragment.getInstance()) }.let { true }
            R.id.navigation_dashboard ->
                true
            R.id.navigation_notifications ->
                supportFragmentManager.transaction { replace(R.id.main_content, Fragment.instantiate(this@AppMain, Profile::class.java.name)) }.let { true }
            else ->
                false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_home
    }
}
