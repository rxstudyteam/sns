package com.teamrx.rxtargram.detail

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.comment.CommentActivity
import com.teamrx.rxtargram.editor.EditorActivity
import com.teamrx.rxtargram.inject.Injection
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.profile.ProfileActivity
import com.teamrx.rxtargram.util.getStringArray
import kotlinx.android.synthetic.main.fragment_detail_view.*

class DetailViewFragment : Fragment(), OptionClickListener {

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var adapter: PostRecyclerViewAdapter
    private val REQUEST_MODYFY = 1001

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_detail_view, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 컴포넌트 리스너
        setupRecyclerView()

        // 활성화 되었을 때 데이터를 다시 로드 하기 위해 뷰모델 observe
        setupViewModel()
    }

    private val goProfile: (userId: String) -> Unit = { userId ->
        activity?.let {
            it.startActivity(Intent(it, ProfileActivity::class.java).apply {
                putExtra("userID", userId)
            })
        }
    }

    private fun setupRecyclerView() {
        adapter = PostRecyclerViewAdapter(requireContext(), this).apply {
            goProfile = this@DetailViewFragment.goProfile
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    private fun setupViewModel() {
        detailViewModel = getViewModel()
        detailViewModel.getPosts().observe(this, Observer { posts ->
            updateUI(posts)
        })

        detailViewModel.loadPosts()

    }

    private fun updateUI(posts: List<Post>) {
        adapter.setPostDatas(posts)
    }

    private inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

    companion object {
        fun newInstance() = DetailViewFragment()
    }

    override fun onOptionClick(post: Post?) {
        val alertBuilder = AlertDialog.Builder(context)
        val adapter = ArrayAdapter<String>(context, android.R.layout.select_dialog_item)
        adapter.addAll(getStringArray(R.array.post_option).toMutableList())

        alertBuilder.setAdapter(adapter) { _, id ->

            val strName = adapter.getItem(id)

            when (strName) {
                getString(R.string.modify) -> startModifyActivity(post)
            }

            Toast.makeText(context, "selected $strName", Toast.LENGTH_SHORT).show()
        }
        alertBuilder.show()
    }

    override fun onCommentClick(post_id: String) {
        // comment 목록보기 화면
        val activity = activity ?: return
        CommentActivity.startActivity(activity, post_id)
    }

    private fun startModifyActivity(post: Post?) {
        val intent = Intent(activity?.applicationContext, ModifyActivity::class.java)
        intent.putExtra("post", post)

        activity?.startActivity(intent)
    }
}
