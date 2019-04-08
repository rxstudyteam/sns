@file:Suppress("PrivatePropertyName")

package com.teamrx.rxtargram.comment

import android.app.AlertDialog
import android.content.Context
import android.log.Log
import android.os.Bundle
import android.util.toast
import android.util.toastLong
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity
import com.teamrx.rxtargram.detail.ModifyViewModel
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.PostDTO
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.comment_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smart.base.PP

@Suppress("LocalVariableName")
class CommentActivity : AppActivity() {

    interface EXTRA {
        companion object {
            const val post_id = "post_id"
        }
    }

    private val commentViewModel by lazy { getViewModel<CommentViewModel>() }
    private val detailViewModel by lazy { getViewModel<ModifyViewModel>() }
    private val input: InputMethodManager by lazy { getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    private lateinit var post_id: String
    private var postId: String? = null
    private var modifyMode: Boolean? = false
    private var selectedComment: CommentDTO? = null

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
        recyclerView.adapter = CommentAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this@CommentActivity)
    }

    private fun setupEvent() {
        tvSummit.setOnClickListener {
            // 로그인상태가 아니므로 임의의 사용자
//            val user_id = "MrTiDrASkFH9hby1x9VD"
            CoroutineScope(Dispatchers.Main).launch {
                val user_id = PP.user_id
                val result = commentViewModel.addComment(parent_post_id = post_id, user_id = user_id, content = edtContent.text.toString())
                if (result) {
                    edtContent.setText("")
                    this@CommentActivity.toast(R.string.comment_add_success)
                } else {
                    this@CommentActivity.toast(R.string.comment_add_failed)
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
            println("ui update..")
            (recyclerView.adapter as? CommentAdapter)?.setComments(comments)
        })

//        detailViewModel.loadPostById(post_id.toString())
        commentViewModel.loadComments(parent_post_id = post_id)
        detailViewModel.getPost(post_id)
    }

    private fun updatePost(post: PostDTO) {
        tvUserId.text = post.user_id
        tvContent.text = post.content
        tvCreatedAt.text = post.created_at.toString()
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

    private fun updateComment(post_id: String, comment: CommentDTO) = CoroutineScope(Dispatchers.Main).launch {
        showProgress()

        val result = commentViewModel.modifyComment(post_id, comment)
        if (result) {
            commentClear()
        } else {
            toastLong("수정실패")
        }
        dismissProgress()
    }

    private fun deleteComment(comment_id: String) = CoroutineScope(Dispatchers.Main).launch {
        showProgress()

        val result = commentViewModel.deleteComment(comment_id)
        if (result)
            toastLong("삭제성공")
        else
            toastLong("삭제실패")

        dismissProgress()
    }

    fun onItemLongClick(comment: CommentDTO) {
        val context = this
        AlertDialog.Builder(context).setItems(R.array.comment_option) { dlg, which ->
            (dlg as AlertDialog).let { dlg ->
                val text = dlg.listView.getItemAtPosition(which)
                val id = dlg.listView.getItemIdAtPosition(which)
                toast("selected ${dlg.listView.getItemAtPosition(which)}")
                Log.e(text, id, R.string.modify, R.string.delete)
                resources.getStringArray(R.array.comment_option)
                when (text) {
                    getString(R.string.delete) -> deleteComment(comment.post_id!!)
                    getString(R.string.modify) -> updateComment(comment.post_id!!, comment)
                    else -> Unit
                }
            }
        }.show()
    }

//    val alertBuilder = AlertDialog.Builder(this@CommentActivity)
//    val adapter = ArrayAdapter<String>(this@CommentActivity, android.R.layout.select_dialog_item)
//    adapter.addAll(  getStringArray(R.array.comment_option).toMutableList())
//
//    alertBuilder.setAdapter(adapter)
//    {
//        _, id ->
//
//        val strName = adapter.getItem(id)
//
//        when (strName) {
//            getString(R.string.modify) -> {
//                updateMode(comment)
//            }
//            getString(R.string.delete) -> {
//                comment.snapshotId?.let { comment_id ->
//                    deleteComment(comment_id)
//                }
//            }
//        }
//    }
//
//    alertBuilder.show()

//    private inline fun <reified T : BaseViewModel> getViewModel(): T {
//        val viewModelFactory = Injection.provideViewModelFactory()
//        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
//    }

//    companion object {
//        fun startActivity(activity: Activity, post_id: String) {
//            val intent = Intent(activity, CommentActivity::class.java)
//            val bundle = Bundle()
//            bundle.putString("post_id", post_id)
//            intent.putExtras(bundle)
//            activity.startActivity(intent)
//        }
//    }

    inner class CommentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val comments = arrayListOf<CommentDTO>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false))

        override fun getItemCount(): Int = comments.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = (holder as ViewHolder).itemView
            item.tvUserId.text = comments[position].user_id
            item.tvContent.text = comments[position].title

            item.tvReComment.setOnClickListener {
                // 답글 달기
            }

            item.setOnLongClickListener {
                comments[position].post_id?.let { post_id ->
                    onItemLongClick(comments[position])
                }
                true
            }
        }

        fun setComments(comments: List<CommentDTO>) {
            this.comments.clear()
            this.comments.addAll(comments)

            notifyDataSetChanged()
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }

}
