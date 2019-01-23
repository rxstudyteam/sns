package com.teamrx.rxtargram.detail

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.inject.Injection
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.util.GlideApp
import kotlinx.android.synthetic.main.activity_crystal.*

/**
 *
 * Created by Rell on 2019. 1. 23..
 */
class CrystalActivity : AppActivity() {
    private lateinit var detailViewModel: DetailViewModel

    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_crystal)

        post = intent.getParcelableExtra("post")
        updateUI()

        println("CrystalActivity > $post")
    }

    override fun onStart() {
        super.onStart()

        detailViewModel = getViewModel()
//        detailViewModel.getPosts().observe(this, Observer { posts ->
//            updateUI(posts)
//        })
    }

    private inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

    private fun updateUI() {

        GlideApp.with(mContext)
                .load("http://cdnweb01.wikitree.co.kr/webdata/editor/201411/28/img_20141128161209_521102e2.jpg")
                .into(ivContentImage)

        tvUserId.text = post.user_id
        tvContent.setText(post.content)
    }
}