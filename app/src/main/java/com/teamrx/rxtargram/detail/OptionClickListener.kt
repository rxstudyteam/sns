package com.teamrx.rxtargram.detail

import com.teamrx.rxtargram.model.Post

interface OptionClickListener {
    fun onOptionClick(post: Post?)
    fun onCommentClick(post_id: String)
}
