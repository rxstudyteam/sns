package com.teamrx.rxtargram.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.util.GlideApp
import kotlinx.android.synthetic.main.activity_modify.*

/**
 *
 * Created by Rell on 2019. 1. 23..
 */
@Suppress("LocalVariableName")
class ModifyActivity : AppActivity() {

    object EXTRA {
        const val post_id = "post_id"
    }

    private val viewModel by lazy { getViewModel<ModifyViewModel>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify)

        viewModel.post.observe(this, Observer { updateUI(it) })

        val post_id = intent.extras?.getString(EXTRA.post_id)
        if (post_id.isNullOrBlank()) {
            runOnUiThread { finish() }
            return
        }

        viewModel.getPost(post_id)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_modify, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        return when (id) {
            R.id.done -> done().let { true }
            else -> super.onOptionsItemSelected(item)
        }

    }

    fun done() {
        if (!check())
            return

        viewModel.post.value ?: return

        viewModel.setPost(viewModel.post.value!!.copy(content = tvContent.text.toString())) { success ->
            if (success) {
                finish()
            } else {
                Toast.makeText(this, "수정에 실패하였습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun check(): Boolean {
        return true
    }

    private fun updateUI(post: PostDTO) {
        GlideApp.with(mContext).load(post.images?.firstOrNull()).into(ivContentImage)
        tvUserId.text = post.user_id
        tvContent.setText(post.content)
    }
}