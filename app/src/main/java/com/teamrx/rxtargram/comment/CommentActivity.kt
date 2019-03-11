package com.teamrx.rxtargram.comment

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.detail.DetailViewModel
import com.teamrx.rxtargram.inject.Injection
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.PostDTO
import kotlinx.android.synthetic.main.activity_comment.*

class CommentActivity : AppCompatActivity() {

    private lateinit var commentViewModel: CommentViewModel
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var adapter: CommentAdapter

    private var postId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        setSupportActionBar(toolBar)

        supportActionBar?.title = getString(R.string.comment)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val arguments = intent.extras
        postId = arguments?.getString("post_id")

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
            val user_id = "MrTiDrASkFH9hby1x9VD"
            commentViewModel.addComment(postId.toString(), user_id, edtContent.text.toString()) { isSuccess ->
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
        detailViewModel = getViewModel()
        commentViewModel = getViewModel()

        detailViewModel.getPostById().observe(this, Observer { post ->
            updatePost(post)
        })

        commentViewModel.getComments().observe(this, Observer { comments ->
            updateComments(comments)
        })

        detailViewModel.loadPostById(postId.toString())
        commentViewModel.loadComments(postId.toString())
    }

    private fun updatePost(post: PostDTO) {
        tvUserId.text = post.user_id
        tvContent.text = post.content
        tvCreatedAt.text = post.created_at.toString()
    }

    private fun updateComments(comments: List<CommentDTO>) {
        adapter.setComments(comments)
    }

    private inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }
}
