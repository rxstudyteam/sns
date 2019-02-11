package com.teamrx.rxtargram.comment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.model.CommentDTO
import kotlinx.android.synthetic.main.comment_item.view.*

class CommentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val comments: ArrayList<CommentDTO> by lazy { arrayListOf<CommentDTO>() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
        = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false))

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = (holder as ViewHolder).itemView
        item.tvUserId.text = comments[position].user_id
        item.tvContent.text = comments[position].title

        item.tvReComment.setOnClickListener {
            // 답글 달기
        }
    }

    fun setComments(comments: List<CommentDTO>) {
        this.comments.clear()
        this.comments.addAll(comments)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view)
}
