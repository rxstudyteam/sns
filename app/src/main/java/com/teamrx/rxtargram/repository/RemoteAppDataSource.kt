@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package com.teamrx.rxtargram.repository

import android.content.Context
import android.log.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.model.ProfileModel
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import smart.util.GalleryLoader
import smart.util.dp
import java.io.InputStream
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("ClassName")
object RemoteAppDataSource : AppDataSource {

    const val USER_COLLECTION = "user"
    const val POST_COLLECTION = "post"

    object USER_DOCUMENT {
        const val EMAIL = "email"
        const val NAME = "name"
        const val PROFILE_URL = "profile_url"
    }

    object POST_DOCUMENT {
        const val CREATED_AT = "created_at"
    }

    override suspend fun loadGalleryLoad(context: Context): String? {
        return suspendCancellableCoroutine { continuation ->
            try {
                GalleryLoader.builder(context)
                        .setCrop(true, 100.dp, 100.dp)
                        .setOnGalleryLoadedListener { continuation.resume(it.toString()) }
                        .setOnCancelListener { continuation.cancel() }
                        .load()
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
            continuation.invokeOnCancellation { continuation.resumeWithException(EmptyStackException()) }
        }
    }

    override suspend fun getProfile(user_id: String): ProfileModel {
        return suspendCancellableCoroutine { continuation ->
            if (user_id.isNullOrBlank()) {
                continuation.resume(ProfileModel())
                return@suspendCancellableCoroutine
            }

            val db = FirebaseFirestore.getInstance()
            val task = db.collection(USER_COLLECTION)
                    .document(user_id)
                    .get()

            task.addOnSuccessListener { document ->
                Log.e("addOnSuccessListener")
                var profileModel: ProfileModel? = null
                if (document.exists())
                    profileModel = document.toObject(ProfileModel::class.java)

                continuation.resume(profileModel ?: ProfileModel())
                Log.w("addOnSuccessListener")
            }
            suspendCancellableCoroutineTask(continuation, task)
        }
    }

    override suspend fun setProfile(user_Id: String, name: CharSequence?, email: CharSequence?, profile_url: String?): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val ref = db.collection(USER_COLLECTION).document(user_Id)
            val map = hashMapOf<String, Any>()
            name?.let { map[USER_DOCUMENT.NAME] = name }
            email?.let { map[USER_DOCUMENT.EMAIL] = email }
            profile_url?.let { map[USER_DOCUMENT.PROFILE_URL] = profile_url }
            val task = ref.update(map as Map<String, Any>)
            task.addOnSuccessListener {
                continuation.resume(true)
            }
            suspendCancellableCoroutineTask(continuation, task)
        }
    }

    override suspend fun join(name: CharSequence, email: CharSequence): String {
        return suspendCancellableCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val ref = db.collection(USER_COLLECTION)
            val task = ref.add(hashMapOf<String, Any>(USER_DOCUMENT.NAME to name.toString(), USER_DOCUMENT.EMAIL to email.toString()))
            task.addOnSuccessListener { continuation.resume(it.id) }

            suspendCancellableCoroutineTask(continuation, task)
        }
    }

    override suspend fun uploadToFireStorage(user_id: String, stream: InputStream) {
        return suspendCancellableCoroutine { continuation ->
            val task = FirebaseStorage.getInstance()
                    .reference
                    .child("profile/${user_id}")
                    .putStream(stream)

            task.addOnSuccessListener { continuation.resume(Unit) }
            suspendCancellableCoroutineTask(continuation, task)
        }
    }

    override suspend fun getDownloadUrl(user_id: String): String? {
        return suspendCancellableCoroutine { continuation ->
            val task = FirebaseStorage.getInstance()
                    .reference
                    .child("profile/${user_id}")
                    .downloadUrl

            task.addOnSuccessListener { continuation.resume(it.toString()) }
            suspendCancellableCoroutineTask(continuation, task)
        }
    }

    private fun <T, R> suspendCancellableCoroutineTask(continuation: CancellableContinuation<T>, task: Task<R>) {
        //for log
        Exception().stackTrace[2].run { task.addOnCompleteListener { Log.ps(if (it.isSuccessful) Log.INFO else Log.WARN, this, it.isComplete && it.isSuccessful) } }
        task.addOnCanceledListener { continuation.cancel() }
        task.addOnFailureListener { continuation.cancel() }
        continuation.invokeOnCancellation { continuation.cancel() }
    }

    // https://github.com/kunny/RxFirebase
// Firebase + Rxjava를 이용해 Obserbable을 리턴하고 ViewModel에서 Livedata로 데이터를 관리하고 싶었으나
// 위 라이브러리의 기능이 얼만큼 있는지 확인이 안되어 Repo에서 LiveData로 관리하도록 구현함
    override fun getPosts(): LiveData<List<Post>> {
        val postLiveData = MutableLiveData<List<Post>>()

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection(POST_COLLECTION).orderBy(POST_DOCUMENT.CREATED_AT)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (querySnapshot == null) return@addSnapshotListener

                    val posts = mutableListOf<Post>()
                    for (snapshot in querySnapshot.documents) {
                        try {
                            val item = snapshot.toObject(Post::class.java)
                            // 팔로우한 유저만 구분.
                            if (item != null)
                                posts.add(item)
                        } catch (e: Exception) {
                            firebaseFirestoreException?.printStackTrace()
                            e.printStackTrace()
                        }
                    }

                    postLiveData.postValue(posts)
                }

        return postLiveData
    }

}