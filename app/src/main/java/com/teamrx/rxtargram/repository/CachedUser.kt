@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package com.teamrx.rxtargram.repository

import androidx.collection.LruCache
import com.teamrx.rxtargram.model.ProfileModel

@Suppress("ClassName", "unused")
object CachedUser : LruCache<String, ProfileModel>(10000) {
//    override fun create(key: String): ProfileModel? {
//        var result: ProfileModel? = null
//        runBlocking(Dispatchers.IO) {
//            result = RemoteAppDataSource.getProfile(user_id = key)
//        }
//        return result
//    }
}
