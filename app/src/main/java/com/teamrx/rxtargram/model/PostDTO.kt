package com.teamrx.rxtargram.model

import java.util.*

data class PostDTO(
    val userId: String,
    val parent_post_no: String?,
    val title: String?,
    val content: String?,
    val created_at: Date
)