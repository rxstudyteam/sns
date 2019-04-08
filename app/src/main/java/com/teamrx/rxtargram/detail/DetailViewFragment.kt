package com.teamrx.rxtargram.detail

import android.content.Intent
import android.log.Log
import android.os.Bundle
import android.util.toast
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppFragment
import com.teamrx.rxtargram.comment.CommentActivity
import com.teamrx.rxtargram.databinding.DetailItemBinding
import com.teamrx.rxtargram.editor.EditorActivity
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.profile.ProfileActivity
import com.teamrx.rxtargram.util.GlideApp
import kotlinx.android.synthetic.main.detail_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    private val goProfile: (userId: String) -> Unit = { userId ->
        requireActivity().startActivity(Intent(requireActivity(), ProfileActivity::class.java).putExtra(ProfileActivity.EXTRA_USER_ID, userId))
    }

//    private fun setupRecyclerView() {
//        adapter = PostRecyclerViewAdapter(requireContext(), this).apply {
//            goProfile = this@DetailViewFragment.goProfile
//        }
//
//        recyclerView.layoutManager = LinearLayoutManager(activity)
//        recyclerView.adapter = adapter
//    }

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
            return ViewHolder(DataBindingUtil.inflate<com.teamrx.rxtargram.databinding.DetailItemBinding>(LayoutInflater.from(parent.context), R.layout.detail_item, parent, false).also { Log.e(it) }.root)
        }

        override fun getItemCount(): Int = posts.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val binding = DataBindingUtil.getBinding<DetailItemBinding>(holder.itemView)
            val d = posts[position]
//            Log.w(binding)
            binding?.apply {
                tvUserId.tag = d.user_id
                tvTitle.text = d.title
                tvContent.text = d.content
                tvCreatedAt.text = d.created_at.toString()

                if (d.images.isNullOrEmpty())
                    ivContentImage.setImageResource(R.drawable.ic_face_black_24dp)
                else
                    GlideApp.with(requireContext()).load(d.images.firstOrNull()).into(ivContentImage)

                CoroutineScope(Dispatchers.Main).launch {
                    val vm = getViewModel<DetailViewModel>()
                    val user = vm.getProfile(d.user_id)
                    if (tvUserId.tag == user.user_id) {
                        tvUserId.text = user.name
                        GlideApp.with(requireContext()).load(user.profile_url).into(ivProfileImage)
                    } else {
                        tvUserId.text = d.user_id
                        GlideApp.with(requireContext()).load(R.drawable.ic_face_black_24dp).into(ivProfileImage)
                    }
                }

                tvUserId.setOnClickListener { goProfile(d.user_id) }
                ivProfileImage.setOnClickListener { goProfile(d.user_id) }
                edit.setOnClickListener { onEditClicked(d.post_id!!) }
                menu.setOnClickListener { onMenuClick(d.post_id!!) }
                comments.setOnClickListener { onCommentClick(d.post_id!!) }
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
