package com.teamrx.rxtargram.list_articles.model

import com.google.firebase.Timestamp


data class ArticleModel(val content: String, val created_at: Timestamp, val parent_post_no: String, val user_id: String)
