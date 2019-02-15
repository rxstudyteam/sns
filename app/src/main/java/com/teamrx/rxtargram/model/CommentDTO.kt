package com.teamrx.rxtargram.model

data class CommentDTO(
    var parent_post_no: String? = null,
    var title: String? = null,
    var user_id: String? = null
)