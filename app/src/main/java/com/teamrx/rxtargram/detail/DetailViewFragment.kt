package com.teamrx.rxtargram.detail

import android.content.Intent
import android.os.Bundle
import android.util.toast
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppFragment
import com.teamrx.rxtargram.comment.CommentActivity
import com.teamrx.rxtargram.databinding.DetailItemBinding
import com.teamrx.rxtargram.editor.EditorActivity
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.util.GlideApp
import kotlinx.android.synthetic.main.detail_fragment.*
import androidx.recyclerview.widget.DividerItemDecoration

class DetailViewFragment : AppFragment() {
    companion object {
        fun newInstance() = DetailViewFragment()
    }

    private val viewModel by lazy { getViewModel<DetailViewModel>() }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.detail_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadOnce()
    }

    private fun loadOnce() {
        recyclerView.adapter = DetailViewAdapter()
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        viewModel.posts.observe(this, Observer { posts ->
            (recyclerView.adapter as DetailViewAdapter).setDatas(posts)
        })
        viewModel.postsListen()
    }

    fun onMenuClick(post_id: String) {
        val context = requireContext()
        AlertDialog.Builder(context).setItems(R.array.post_option) { dlg, which ->
            context.toast("selected ${(dlg as AlertDialog).listView.getItemAtPosition(which)}")
            when (which) {
                3 -> startActivity(Intent(requireContext(), ModifyActivity::class.java).putExtra("post_id", post_id))
            }
        }.show()
    }

    fun onCommentClick(post_id: String) {
        startActivity(Intent(requireContext(), CommentActivity::class.java).putExtra(CommentActivity.EXTRA.post_id, post_id))
    }

    fun onEditClicked(post_id: String) {
        startActivity(Intent(requireContext(), ModifyActivity::class.java).putExtra(ModifyActivity.EXTRA.post_id, post_id))
    }

    inner class DetailViewAdapter : RecyclerView.Adapter<DetailViewAdapter.ViewHolder>() {
        private val posts = arrayListOf<PostDTO>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binder: DetailItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.detail_item, parent, false)
            binder.root.tag = binder
            return ViewHolder(binder.root)
        }

        override fun getItemCount(): Int = posts.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val view = holder.itemView.tag as DetailItemBinding
            val d = posts[position]
            view.apply {
                tvUserId.text = d.user_id
                tvTitle.text = d.title
                tvContent.text = d.content
                tvCreatedAt.text = d.created_at.toString()
                GlideApp.with(requireContext()).load(d.images?.firstOrNull()).into(view.ivContentImage)
                d.post_id?.let { post_id ->
                    edit.setOnClickListener { onEditClicked(post_id) }
                    menu.setOnClickListener { onMenuClick(post_id) }
                    comments.setOnClickListener { onCommentClick(post_id) }
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
