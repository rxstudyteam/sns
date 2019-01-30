package com.teamrx.rxtargram.detail


import android.app.AlertDialog
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
import com.teamrx.rxtargram.inject.Injection
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.util.getStringArray
import kotlinx.android.synthetic.main.fragment_detail_view.*

class DetailViewFragment : Fragment(), OptionClickListener {

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var adapter: PostRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_detail_view, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = PostRecyclerViewAdapter(requireContext(), this)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        detailViewModel = getViewModel()
        detailViewModel.postLiveData.observe(this, Observer { posts ->
            println("detail viewmodel observe..")
            updateUI(posts)
        })

        detailViewModel.getPosts()
    }

    override fun onOptionClick(post: Post?) {
        val context = this.context ?: return

        val alertBuilder = AlertDialog.Builder(context)
        val adapter = ArrayAdapter<String>(context, android.R.layout.select_dialog_item)
        adapter.addAll(getStringArray(R.array.post_option).toMutableList())

        alertBuilder.setAdapter(adapter) { _, id ->

            val strName = adapter.getItem(id)

            when (strName) {
                getString(R.string.crystal) -> println("수정!!")
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

    private fun updateUI(posts: List<Post>) {
        adapter.addPosts(posts)
    }

    // base activity
    private inline fun <reified T : BaseViewModel> getViewModel(): T {
        val viewModelFactory = Injection.provideViewModelFactory()
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }

    companion object {
        fun newInstance() = DetailViewFragment()
    }

}
