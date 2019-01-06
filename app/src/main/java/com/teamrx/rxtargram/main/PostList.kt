package com.teamrx.rxtargram.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppFragment
import com.teamrx.rxtargram.databinding.MainPostlistBinding

class PostList : AppFragment() {
    private lateinit var bb: MainPostlistBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bb = DataBindingUtil.inflate(inflater, R.layout.main_postlist, container, false)
        bb.vm = ViewModelProviders.of(mActivity).get(PostListViewModel::class.java)
        return bb.root
    }
}