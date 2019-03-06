@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package com.teamrx.rxtargram.repository

import android.content.Context
import android.log.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.model.PostImages
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

    private val fireStore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val cachedPosts: HashMap<String, Post> by lazy { HashMap<String, Post>() }

    override fun getPosts(callback: (List<Post>) -> Unit) {

        fireStore.collection("post").orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (querySnapshot == null) return@addSnapshotListener

                    val posts = mutableListOf<Post>()
                    for (snapshot in querySnapshot.documents) {
                        try {
                            val item = snapshot.toObject(Post::class.java)
                            item?.post_id = snapshot.id

                            // 팔로우한 유저만 구분.
                            if (item != null) {
                                posts.add(item)
                                cachedPosts[snapshot.id] = item
                            }
                        } catch (e: Exception) {
                            firebaseFirestoreException?.printStackTrace()
                            e.printStackTrace()
                        }
                    }

                    callback(posts)
                }
    }

    override fun getPostImageUrl(post_image_id: String, callback: (String) -> Unit) {

        val task = FirebaseStorage.getInstance()
                .reference
                .child("images/${post_image_id}")
                .downloadUrl

        task.addOnSuccessListener { callback(it.toString()) }
        task.addOnFailureListener { throw it }
    }

    override fun getPostImages(post_id: String?, callback: (List<PostImages>) -> Unit) {
        val post_id = "wL33VTH2vKNOHLrYsl0x"
        val snapshot = fireStore.collection("post")
                .document(post_id)
                .collection("images")
                .get()

        snapshot.addOnSuccessListener { querySnapshot ->
            //            Log.e("addOnSuccessListener")

            for (document in querySnapshot.documents) {
                Log.e(document.id, document["url"])
            }
            var images = querySnapshot.toObjects(PostImages::class.java)
            callback(images)
//            Log.w("addOnSuccessListener")
        }

    }

    override fun getPostById(post_id: String, callback: (Post) -> Unit) {
        cachedPosts[post_id]?.let {
            callback(it)
            println("get cached post")
            return
        }

        fireStore.collection("post").document(post_id).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let { documentSnapshot ->
                            documentSnapshot.toObject(Post::class.java)?.let {
                                callback(it)
                                println("post from network")
                            }
                        }
                    }
                }
    }

    override fun getComments(post_id: String, callback: (List<CommentDTO>) -> Unit) {

        fireStore.collection("post").whereEqualTo("parent_post_no", post_id)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (querySnapshot == null) return@addSnapshotListener

                    val commentDTOs = mutableListOf<CommentDTO>()
                    for (snapshot in querySnapshot.documents) {
                        try {
                            val item = snapshot.toObject(CommentDTO::class.java)
                            if (item?.parent_post_no == post_id) {
                                commentDTOs.add(item)
                            }
                        } catch (e: Exception) {
                            firebaseFirestoreException?.printStackTrace()
                            e.printStackTrace()
                        }
                    }

                    callback(commentDTOs)
                }
    }

    override fun addComment(parent_post_id: String, user_id: String, content: String, callback: (Boolean) -> Unit) {
        fireStore.collection("post").document().set(CommentDTO(parent_post_id, content, user_id))
                .addOnCompleteListener {
                    callback(it.isSuccessful)
                }
    }

    override fun modifyPost(post: Post, callback: (Boolean) -> Unit) {

        post.snapshotId?.let {
            println("post : $post")
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection(POST_COLLECTION).document(it).set(post)
                    .addOnCompleteListener { it ->
                        callback(it.isSuccessful)

                    }
        }
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
}