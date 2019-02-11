package com.teamrx.rxtargram.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity
import com.teamrx.rxtargram.base.AppApplication.Companion.context
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.inject.Injection
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.util.GlideApp
import kotlinx.android.synthetic.main.activity_modify.*

/**
 *
 * Created by Rell on 2019. 1. 23..
 */
class ModifyActivity : AppActivity() {
    private lateinit var detailViewModel: DetailViewModel

    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_modify)

        post = intent.getParcelableExtra("post")
        updateUI()

        println("ModifyActivity > $post")

        detailViewModel = getViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_modify, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId

        when (id) {
            R.id.done -> done()
        }

        return super.onOptionsItemSelected(item)
    }

    fun done() {
        updateContent()

        detailViewModel.modifyPost(post) { success ->
            if (success) {
                finish()
            } else {
                Toast.makeText(context, "수정에 실패하였습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateContent() {
        post.content = tvContent.text.toString()
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