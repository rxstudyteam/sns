@file:Suppress("PrivatePropertyName")

package com.teamrx.rxtargram.comment

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity
import com.teamrx.rxtargram.detail.DetailViewModel
import com.teamrx.rxtargram.detail.ModifyViewModel
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.PostDTO
import kotlinx.android.synthetic.main.activity_comment.*
import smart.base.PP

@Suppress("LocalVariableName")
class CommentActivity : AppActivity() {

    object EXTRA {
        const val post_id = "post_id"
    }
//    interface EXTRA {
//        companion object {
//            const val post_id = "post_id"
//        }
//    }

    private val commentViewModel by lazy { getViewModel<CommentViewModel>() }
    private val detailViewModel by lazy { getViewModel<ModifyViewModel>() }
    private lateinit var adapter: CommentAdapter

    private lateinit var post_id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        setSupportActionBar(toolBar)

        supportActionBar?.title = getString(R.string.comment)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val post_id = intent.extras?.getString(EXTRA.post_id)
        if (post_id.isNullOrBlank()) {
            runOnUiThread { finish() }
            return
        }

        this.post_id = post_id
        setupRecyclerView()
        setupEvent()
        setupViewModel()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        adapter = CommentAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this@CommentActivity)
    }

    private fun setupEvent() {
        tvSummit.setOnClickListener {
            // 로그인상태가 아니므로 임의의 사용자
//            val user_id = "MrTiDrASkFH9hby1x9VD"
            val user_id = PP.user_id
            commentViewModel.addComment(post_id.toString(), user_id, edtContent.text.toString()) { isSuccess ->
                if (isSuccess) {
                    edtContent.setText("")
                    Toast.makeText(this, getString(R.string.comment_add_success), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, getString(R.string.comment_add_failed), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupViewModel() {
//        detailViewModel = getViewModel()
//        commentViewModel = getViewModel()

        detailViewModel.getPost(post_id).observe(this, Observer { post ->
            updatePost(post)
        })

        commentViewModel.getComments().observe(this, Observer { comments ->
            updateComments(comments)
        })

//        detailViewModel.loadPostById(post_id.toString())
        commentViewModel.loadComments(post_id.toString())
    }

    private fun updatePost(post: PostDTO) {
        tvUserId.text = post.user_id
        tvContent.text = post.content
        tvCreatedAt.text = post.created_at.toString()
    }

    private fun updateComments(comments: List<CommentDTO>) {
        adapter.setComments(comments)
    }

}
