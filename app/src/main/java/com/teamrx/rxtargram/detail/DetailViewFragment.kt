package com.teamrx.rxtargram.detail

import android.content.Intent
import android.os.Bundle
import android.util.toast
import android.view.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppFragment
import com.teamrx.rxtargram.comment.CommentActivity
import com.teamrx.rxtargram.databinding.DetailItemBinding
import com.teamrx.rxtargram.editor.EditorActivity
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.util.GlideApp
import kotlinx.android.synthetic.main.fragment_detail_view.*

class DetailViewFragment : AppFragment() {
    companion object {
        fun newInstance() = DetailViewFragment()
    }

    private lateinit var detailViewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.detail_view, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.write -> startActivity(Intent(requireContext(), EditorActivity::class.java)).run { true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_detail_view, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        detailViewModel = getViewModel()

        swipe.setOnRefreshListener { setupViewModel() }

        // 컴포넌트 리스너
        setupRecyclerView()

        // 활성화 되었을 때 데이터를 다시 로드 하기 위해 뷰모델 observe
        setupViewModel()
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = DetailViewAdapter()
    }

    private fun setupViewModel() {
        detailViewModel.getPosts().observe(this, Observer { posts ->
            updateUI(posts)
        })

        detailViewModel.loadPosts()
    }

    private fun updateUI(posts: List<PostDTO>) {
        (recyclerView.adapter as DetailViewAdapter).setDatas(posts)
        swipe.isRefreshing = false
    }

    @Suppress("EnumEntryName", "UNUSED_PARAMETER", "SpellCheckingInspection", "ClassName")
    enum class ePOPUP(@StringRes resid: kotlin.Int) {
        done(R.string.done),
        share(R.string.share),
        copy_link(R.string.copy_link),
        save(R.string.save),
        modify(R.string.modify),
        delete(R.string.delete);
    }

    fun onOptionClick(post_id: String) {
        val context = requireContext()
        AlertDialog.Builder(context).setItems(R.array.post_option) { dlg, which ->
            context.toast("selected ${(dlg as AlertDialog).listView.getItemAtPosition(which)}")
            when (which) {
                3 -> startActivity(Intent(requireContext(), ModifyActivity::class.java).putExtra("post_id", post_id))
            }
        }.show()
    }

    fun onCommentClick(post_id: String) {
        startActivity(Intent(requireContext(), CommentActivity::class.java).putExtra("post_id", post_id))
    }

    fun onDetailClicked(post_id: String) {
        startActivity(Intent(requireContext(), CommentActivity::class.java).putExtra("post_id", post_id))
    }

    inner class DetailViewAdapter : RecyclerView.Adapter<DetailViewAdapter.ViewHolder>() {
        private val posts = arrayListOf<PostDTO>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(DetailItemBinding.inflate(LayoutInflater.from(parent.context)).let { it.root.apply { tag = it } })

        override fun getItemCount(): Int = posts.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val view = holder.itemView.tag as DetailItemBinding
            posts[position].run {
                view.tvUserId.text = user_id
                view.tvTitle.text = title
                view.tvContent.text = content
                view.tvCreatedAt.text = created_at.toString()
                GlideApp.with(requireContext()).load(images?.firstOrNull()).into(view.ivContentImage)
                post_id?.let { post_id ->
                    view.root.setOnClickListener { onDetailClicked(post_id) }
                    view.option.setOnClickListener { onOptionClick(post_id) }
                    view.tvComments.setOnClickListener { onCommentClick(post_id) }
                }
            }
        }

        fun setDatas(posts: List<PostDTO>) {
            println("${posts.size}")
            this.posts.clear()
            this.posts.addAll(posts)
            notifyDataSetChanged()
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
}
