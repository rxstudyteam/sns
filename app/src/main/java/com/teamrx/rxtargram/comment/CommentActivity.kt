package com.teamrx.rxtargram.comment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

class CommentActivity : AppCompatActivity() {

    private lateinit var commentViewModel: CommentViewModel
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var adapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val arguments = intent.extras
        val postId = arguments.getString("post_id")

        adapter = CommentAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this@CommentActivity)

        detailViewModel = getViewModel()
        commentViewModel = getViewModel()

        detailViewModel.getPostById().observe(this, Observer { post ->
            updatePost(post)
        })

        commentViewModel.getComments().observe(this, Observer { comments ->
            updateComments(comments)
        })

        detailViewModel.loadPostById(postId)
        commentViewModel.loadComments(postId)
    }

    private fun updatePost(post: Post) {
        tvUserId.text = post.user_id
        tvContent.text = post.content
        tvCreatedAt.text = post.created_at?.toDate().toString()
    }

    private fun updateComments(comments: List<CommentDTO>) {
        adapter.setComments(comments)
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
