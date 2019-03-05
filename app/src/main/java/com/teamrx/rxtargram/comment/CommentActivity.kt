package com.teamrx.rxtargram.comment

import android.app.Activity
import android.base.CActivity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.detail.DetailViewModel
import com.teamrx.rxtargram.inject.Injection
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.Post
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentActivity : CActivity() {

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
        return when(item?.itemId) {
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
            addComment(postId.toString(), user_id, edtContent.text.toString())
        }
    }

    private fun setupViewModel() {
        detailViewModel = getViewModel()
        commentViewModel = getViewModel()

        detailViewModel.getPostById().observe(this, Observer { post ->
            updatePost(post)
        })

        commentViewModel.getComments().observe(this, Observer { comments ->
            println("ui update..")
            updateComments(comments)
        })

        detailViewModel.loadPostById(postId.toString())
        loadComments(postId.toString())
    }

    private fun updatePost(post: Post) {
        tvUserId.text = post.user_id
        tvContent.text = post.content
        tvCreatedAt.text = post.created_at?.toDate().toString()
    }

    private fun updateComments(comments: List<CommentDTO>) {
        adapter.setComments(comments)
    }

    private fun loadComments(postId: String) = CoroutineScope(Dispatchers.Main).launch {
        commentViewModel.loadComments(postId)
    }

    private fun addComment(parent_post_id: String, user_id: String, content: String) = CoroutineScope(Dispatchers.Main).launch {
        showProgress()

        commentViewModel.addComment(parent_post_id, user_id, content)
            .observe(this@CommentActivity, Observer { isSuccess ->
                if(isSuccess) {
                    edtContent.setText("")
                } else {
                    Toast.makeText(this@CommentActivity, getString(R.string.comment_add_failed), Toast.LENGTH_LONG).show()
                }
            })

        dismissProgress()
    }

    private inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

    companion object {
        fun startActivity(activity: Activity, post_id: String) {
            val intent = Intent(activity, CommentActivity::class.java)
            val bundle = Bundle()
            bundle.putString("post_id", post_id)

            intent.putExtras(bundle)

            activity.startActivity(intent)
        }

    }
}
