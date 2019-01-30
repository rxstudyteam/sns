package com.teamrx.rxtargram.comment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.inject.Injection

class CommentActivity : AppCompatActivity() {

    private lateinit var commentViewModel: CommentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val arguments = intent.extras
        val postId = arguments.getString("post_id")

        commentViewModel = getViewModel()
        commentViewModel.commentLiveData.observe(this, Observer { lists ->
            for(comment in lists) {
                println("${comment.title}  ${comment.user_id}  ${comment.parent_post_no}")
            }
        })
        commentViewModel.getComments(postId)
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
