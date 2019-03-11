package com.teamrx.rxtargram.detail

import com.teamrx.rxtargram.model.PostDTO

interface OptionClickListener {
    fun onOptionClick(post: PostDTO?)
    fun onCommentClick(post_id: String)
}