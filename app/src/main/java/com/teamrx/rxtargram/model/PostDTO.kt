package com.teamrx.rxtargram.model

import com.google.firebase.Timestamp
import java.util.*


data class PostDTO(
        val user_id: String = "",
        val title: String? = null,
        val content: String? = null,
        val images: List<String>? = emptyList(),
        val parent_post_no: String = "",
        val likes: List<String>? = emptyList(),
        val created_at: Date = Timestamp.now().toDate(),
        var post_id: String? = null
)