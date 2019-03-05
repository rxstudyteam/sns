package com.teamrx.rxtargram.comment

import android.app.Activity
import android.app.AlertDialog
import android.base.CActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
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
import com.teamrx.rxtargram.util.getStringArray
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentActivity : CActivity() {

    private lateinit var commentViewModel: CommentViewModel
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var adapter: CommentAdapter
    private val input: InputMethodManager by lazy { getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    private var postId: String? = null
    private var modifyMode: Boolean? = false
    private var selectedComment: CommentDTO? = null

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
        adapter = CommentAdapter { comment -> onOptionClick(comment) }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this@CommentActivity)
    }

    private fun setupEvent() {
        tvSummit.setOnClickListener {
            // 로그인상태가 아니므로 임의의 사용자
            val user_id = "MrTiDrASkFH9hby1x9VD"

            modifyMode?.let { modifyMode ->
                if(modifyMode) {
                    selectedComment?.title = edtContent.text.toString()
                    selectedComment?.let {
                        updateComment(it)
                    }

                } else {
                    addComment(postId.toString(), user_id, edtContent.text.toString())
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

    private fun updateMode(comment: CommentDTO) {
        modifyMode = true
        selectedComment = comment
        edtContent.setText(comment.title)
        edtContent.setSelection(edtContent.length())
        input.showSoftInput(edtContent, 0)
    }

    private fun commentClear() {
        modifyMode = false
        edtContent.setText("")
        input.hideSoftInputFromWindow(edtContent.windowToken, 0)
    }

    private fun loadComments(postId: String) = CoroutineScope(Dispatchers.Main).launch {
        commentViewModel.loadComments(postId)
    }

    private fun addComment(parent_post_id: String, user_id: String, content: String) = CoroutineScope(Dispatchers.Main).launch {
        showProgress()

        commentViewModel.addComment(parent_post_id, user_id, content)
            .observe(this@CommentActivity, Observer { isSuccess ->
                if(isSuccess) {
                    commentClear()
                } else {
                    Toast.makeText(this@CommentActivity, getString(R.string.comment_add_failed), Toast.LENGTH_LONG).show()
                }
            })

        dismissProgress()
    }

    private fun updateComment(comment: CommentDTO) = CoroutineScope(Dispatchers.Main).launch {
        showProgress()

        commentViewModel.modifyComment(comment).observe(this@CommentActivity, Observer { isSuccess ->
            if(isSuccess) {
                commentClear()
            } else {
                Toast.makeText(this@CommentActivity, "수정실패", Toast.LENGTH_LONG).show()
            }
        })

        dismissProgress()
    }

    private fun deleteComment(comment_id: String) = CoroutineScope(Dispatchers.Main).launch {
        showProgress()

        commentViewModel.deleteComment(comment_id).observe(this@CommentActivity, Observer { isSuccess ->
            if(isSuccess) {
                Toast.makeText(this@CommentActivity, "삭제성공", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@CommentActivity, "삭제실패", Toast.LENGTH_LONG).show()
            }
        })

        dismissProgress()
    }

    private fun onOptionClick(comment: CommentDTO) {
        val alertBuilder = AlertDialog.Builder(this@CommentActivity)
        val adapter = ArrayAdapter<String>(this@CommentActivity, android.R.layout.select_dialog_item)
        adapter.addAll(getStringArray(R.array.comment_option).toMutableList())

        alertBuilder.setAdapter(adapter) { _, id ->

            val strName = adapter.getItem(id)

            when (strName) {
                getString(R.string.modify) -> {
                    updateMode(comment)
                }
                getString(R.string.delete) -> {
                    comment.snapshotId?.let { comment_id ->
                        deleteComment(comment_id)
                    }
                }
            }
        }

        alertBuilder.show()
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
