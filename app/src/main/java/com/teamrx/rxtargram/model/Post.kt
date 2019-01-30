package com.teamrx.rxtargram.model

import com.google.firebase.Timestamp

data class Post(var post_id: String? = null,
                var content: String? = null,
                var created_at: Timestamp? = null,
                var parent_post_no: String? = null,
                var title: String? = null,
                val user_id: String? = null)