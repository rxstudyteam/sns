package com.teamrx.rxtargram.model

data class ProfileModel(
        var name: String = "",
        var email: String = "",
        var profile_url: String? = null,
        val follows: List<String>? = emptyList(),
        val followers: List<String>? = emptyList(),
        var user_id : String =""

)











