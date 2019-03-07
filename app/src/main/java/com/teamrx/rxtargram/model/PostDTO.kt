package com.teamrx.rxtargram.model

import com.google.firebase.Timestamp
import java.util.*

data class PostDTO(
        val userId: String,
        val title: String?,
        val content: String?,
        val parent_post_no: String? = null,
        val created_at: Date = Timestamp.now().toDate(),
        val images: List<String> = emptyList(),
        val likes: List<String> = emptyList()
)