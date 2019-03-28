@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package com.teamrx.rxtargram.repository

import android.content.Context
import android.log.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.teamrx.rxtargram.model.CommentDTO
import com.teamrx.rxtargram.model.PostDTO
import com.teamrx.rxtargram.model.ProfileModel
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import smart.util.GalleryLoader
import smart.util.dp
import java.io.InputStream
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("ClassName", "unused")
object RemoteAppDataSource : AppDataSource {

    const val USER_COLLECTION = "user"
    const val POST_COLLECTION = "post"

    object USER_DOCUMENT {
        const val NAME = "name"
        const val EMAIL = "email"
        const val PROFILE_URL = "profile_url"
        const val FOLLOWS = "follows"
        const val FOLLOWERS = "followers"
    }

    object POST_DOCUMENT {
        const val USER_ID = "user_id"
        const val TITLE = "title"
        const val CONTENT = "content"
        const val IMAGES = "images"
        const val PARENT_POST_NO = "parent_post_no"
        const val LIKES = "likes"
        const val CREATED_AT = "created_at"
    }

    object STORAGE {
        const val POST = "images/"
        const val USER = "profile/"
    }

    override suspend fun addPost(postDTO: PostDTO): String {
        return suspendCancellableCoroutine { continuation ->
            val task = FirebaseFirestore.getInstance()
                    .collection(POST_COLLECTION)
                    .add(postDTO)

            task.addOnSuccessListener { continuation.resume(it.id) }

            suspendCancellableCoroutineTask(continuation, task)
        }
    }

    override fun setPostSnapshotListener(callback: (List<PostDTO>) -> Unit) {
        FirebaseFirestore.getInstance()
                .collection(POST_COLLECTION)
//                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    firebaseFirestoreException?.printStackTrace()

                    val result = ArrayList<PostDTO>()
                    querySnapshot?.run {
                        val it = iterator()
                        while (it.hasNext()) {
                            val d = it.next() as DocumentSnapshot
                            val post = d.toObject(PostDTO::class.java)
                            post?.post_id = d.id
                            result.add(post!!)
                        }
                    }
//                    val result = querySnapshot?.toObjects(PostDTO::class.java) ?: emptyList()
//                    Log.i(result.size, result)
                    callback(result)
                }
    }

    override fun setPost(post: PostDTO, callback: (Boolean) -> Unit) {
        Log.e("$POST_COLLECTION : $post")

        FirebaseFirestore.getInstance()
                .collection(POST_COLLECTION)
                .document(post.post_id!!)
                .set(post)
                .addOnCompleteListener {
                    callback(it.isSuccessful)
                }

    }

    override fun getPost(post_id: String, callback: (PostDTO) -> Unit) {
        FirebaseFirestore.getInstance()
                .collection(POST_COLLECTION)
                .document(post_id).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let { documentSnapshot ->
                            documentSnapshot.toObject(PostDTO::class.java)?.let {
                                callback(it)
                                println("post from network")
                            }
                        }
                    }
                }
    }

//    suspend fun getPosts(): List<PostDTO> = suspendCancellableCoroutine { continuation ->
//        val task = FirebaseFirestore.getInstance()
//                .collection(POST_COLLECTION)
////                .orderBy("created_at", Query.Direction.DESCENDING)
//                .get()
//                        // 팔로우한 유저만 구분.
//                        if (item != null) {
//                            posts.add(item)
//                            cachedPosts[snapshot.id] = item
//
//                        }
//                    } catch (e: Exception) {
//                        firebaseFirestoreException?.printStackTrace()
//                        e.printStackTrace()
//                    }
//
//        task.addOnSuccessListener { querySnapshot ->
//            continuation.resume(querySnapshot.toObjects(PostDTO::class.java))
//        }
//        suspendCancellableCoroutineTask(continuation, task)
//    }

//    override fun addComment(parent_post_id: String, user_id: String, content: String, callback: (Boolean) -> Unit) {
//        FirebaseFirestore.getInstance().collection(POST_COLLECTION).document().set(CommentDTO(parent_post_id, content, user_id))
//                .addOnCompleteListener {
//                    callback(it.isSuccessful)
//                }
//    }

    //Comment
    override suspend fun addComment(parent_post_id: String, user_id: String, content: String): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                    .collection(POST_COLLECTION)
                    .document()
                    .set(CommentDTO(parent_post_id, content, user_id))
                    .addOnCompleteListener { task ->
                        continuation.resume(task.isSuccessful)
                    }
        }
    }

    override fun setCommentSnapshotListener(parennta_post_id: String, callback: (List<CommentDTO>) -> Unit) {
        FirebaseFirestore.getInstance().collection(POST_COLLECTION)
                .whereEqualTo(POST_DOCUMENT.PARENT_POST_NO, parennta_post_id)
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    firebaseFirestoreException?.printStackTrace()
                    querySnapshot ?: return@addSnapshotListener

                    val result = querySnapshot.toObjects(CommentDTO::class.java)
                    Log.i(result.size, result)

                    callback(result)
                }
    }

    override suspend fun modifyComment(post_id: String, comment: CommentDTO): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                    .collection(POST_COLLECTION)
                    .document(post_id)
                    .set(comment)
                    .addOnCompleteListener { task ->
                        continuation.resume(task.isSuccessful)
                    }

        }
    }

    override suspend fun deleteComment(post_id: String): Boolean {
        return suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                    .collection(POST_COLLECTION)
                    .document(post_id)
                    .delete()
                    .addOnCompleteListener { task ->
                        continuation.resume(task.isSuccessful)
                    }
        }
    }

//    override suspend fun getComments(post_id: String): ReceiveChannel<List<CommentDTO>> {
//        listener?.remove() // addSnapshotListener를 onDestory에 remove() 해주거나 삭제가 안되었다면 수동삭제
//
//        val channel = Channel<List<CommentDTO>>()
//
//        listener = fireStore.collection(POST_COLLECTION).whereEqualTo(POST_DOCUMENT.PARENT_POST_NO, post_id).
//            addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//
//                if (querySnapshot == null) {
//                    channel.close()
//                    return@addSnapshotListener
//                }
//                firebaseFirestoreException?.let {
//                    channel.close(it)
//                    return@addSnapshotListener
//                }
//
//                val commentDTOs = arrayListOf<CommentDTO>()
//                for(snapshot in querySnapshot.documents) {
//                    val item = snapshot.toObject(CommentDTO::class.java)
//                    if(item?.parent_post_no == post_id) {
//                        item.snapshotId = snapshot.id
//                        commentDTOs.add(item)
//                    }
//                }
//
//                //https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.channels/send-blocking.html
//                channel.sendBlocking(commentDTOs)
//            }
//
//        return channel
//    }

    //user
    override suspend fun join(name: CharSequence, email: CharSequence, user_image_url: CharSequence?): String {
        return suspendCancellableCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val ref = db.collection(USER_COLLECTION)
            val task = ref.add(
                    hashMapOf<String, Any>(
                            USER_DOCUMENT.NAME to name.toString(),
                            USER_DOCUMENT.EMAIL to email.toString()
                    )
            )
            task.addOnSuccessListener { continuation.resume(it.id) }

            suspendCancellableCoroutineTask(continuation, task)
        }
    }

    override suspend fun getProfile(user_id: String): ProfileModel = suspendCancellableCoroutine { continuation ->
        if (user_id.isNullOrBlank()) {
            continuation.resume(ProfileModel())
            return@suspendCancellableCoroutine
        }

        val db = FirebaseFirestore.getInstance()
        val task = db.collection(USER_COLLECTION)
                .document(user_id)
                .get()

        task.addOnSuccessListener { document ->
            //            Log.e("addOnSuccessListener")
            if (!document.exists()) {
                continuation.resume(ProfileModel())
                return@addOnSuccessListener
            }
            try {
                val profileModel = document.toObject(ProfileModel::class.java)
                profileModel?.user_id = user_id
                continuation.resume(profileModel ?: ProfileModel())
            } catch (e: Exception) {
                Log.w("parse fail $user_id")
            }
//            Log.w("addOnSuccessListener")
        }
        suspendCancellableCoroutineTask(continuation, task)
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

    override suspend fun uploadToFireStorageUserImage(stream: InputStream): String {
        return suspendCancellableCoroutine { continuation ->
            val uuid = UUID.randomUUID().toString()
            val id = "images/$uuid"
            Log.e("upload profile image : $id")

            val task = FirebaseStorage.getInstance().reference.child(id).putStream(stream)
            task.addOnSuccessListener { continuation.resume(id) }
            suspendCancellableCoroutineTask(continuation, task)
        }
    }

    override suspend fun uploadToFireStoragePostImage(stream: InputStream): String {
        return suspendCancellableCoroutine { continuation ->
            val uuid = UUID.randomUUID().toString()
            val id = "images/$uuid"
            Log.e("upload post image : $id")

            val task = FirebaseStorage.getInstance().reference.child(id).putStream(stream)
            task.addOnSuccessListener { continuation.resume(id) }
            suspendCancellableCoroutineTask(continuation, task)
        }
    }

//    override fun uploadToFireStorage(id: String, stream: InputStream, callback: (String) -> Unit) {
//        val task = FirebaseStorage.getInstance()
//                .reference
//                .child(id)
//                .putStream(stream)
//
//        task.addOnSuccessListener { callback(it.toString()) }
//        task.addOnFailureListener { throw  it }
//    }
//
//    override fun getDownloadUrl(post_image_id: String, callback: (String) -> Unit) {
//        val task = FirebaseStorage.getInstance()
//                .reference
//                .child(post_image_id)
//                .downloadUrl
//
//        task.addOnSuccessListener { callback(it.toString()) }
//        task.addOnFailureListener { throw it }
//    }

    override suspend fun getDownloadUrl(image_id: String): String = suspendCancellableCoroutine { continuation ->
        val task = FirebaseStorage.getInstance()
                .reference
                .child(image_id)
                .downloadUrl

        task.addOnSuccessListener { continuation.resume(it.toString()) }
        suspendCancellableCoroutineTask(continuation, task)
    }

    private fun <T, R> suspendCancellableCoroutineTask(continuation: CancellableContinuation<T>, task: Task<R>) {
        //for log
        Exception().stackTrace[2].run {
            task.addOnCompleteListener {
                Log.ps(if (it.isSuccessful) Log.INFO else Log.WARN, this, it.isComplete && it.isSuccessful)
            }
        }
        task.addOnCanceledListener { continuation.cancel() }
        task.addOnFailureListener { continuation.cancel() }
        continuation.invokeOnCancellation { continuation.cancel() }
    }

    override suspend fun loadGalleryLoad(context: Context): String? = suspendCancellableCoroutine { continuation ->
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