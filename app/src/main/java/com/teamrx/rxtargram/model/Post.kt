package com.teamrx.rxtargram.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

data class Post(var content: String? = null,
                var created_at: Timestamp? = null,
                var parent_post_no: String? = null,
                var title: String? = null,
                val user_id: String? = null) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(Timestamp::class.java.classLoader),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(content)
        parcel?.writeParcelable(created_at, 0)
        parcel?.writeString(parent_post_no)
        parcel?.writeString(title)
        parcel?.writeString(user_id)
    }

    override fun describeContents(): Int = 0

    override fun toString(): String {
        return "title : $title, user : $user_id, content : $content"
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}