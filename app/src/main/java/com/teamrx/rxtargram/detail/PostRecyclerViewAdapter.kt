package com.teamrx.rxtargram.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.util.GlideApp
import kotlinx.android.synthetic.main.detail_item.view.*
import java.util.*

class PostRecyclerViewAdapter(private val mContext: Context, private val optionClickListener: OptionClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val posts = ArrayList<PostDTO>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.detail_item, parent, false))

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = (holder as ViewHolder).itemView

        GlideApp.with(mContext)
            .load("http://cdnweb01.wikitree.co.kr/webdata/editor/201411/28/img_20141128161209_521102e2.jpg")
            .into(item.ivContentImage)

        item.tvUserId.text = posts[position].user_id
        item.tvTitle.text = posts[position].title
        item.tvContent.text = posts[position].content
        item.tvCreatedAt.text = posts[position].created_at?.toDate().toString()

        item.tvComments.setOnClickListener {
            posts[position].post_id?.let { optionClickListener.onCommentClick(it) }
        }
    }

    fun setPostDatas(postDTOS: List<PostDTO>) {
        this.posts.clear()
        this.posts.addAll(postDTOS)
        notifyDataSetChanged()

        println("${this.posts.size}      ${postDTOS.size}")
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}