package com.teamrx.rxtargram.repository

import android.content.Context
import android.log.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.teamrx.rxtargram.model.Post
import com.teamrx.rxtargram.model.ProfileModel
import kotlinx.coroutines.suspendCancellableCoroutine
import smart.util.GalleryLoader
import smart.util.dp
import java.io.InputStream
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
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

    override fun getProfile(user_id: String): ProfileModel = try {
        Executors.newSingleThreadExecutor().submit<ProfileModel> {
            val db = FirebaseFirestore.getInstance()
            var task = db.collection(USER_COLLECTION).document(user_id).get()
            val document = Tasks.await(task, 5, TimeUnit.SECONDS)

            if (document.exists())
                document.toObject(ProfileModel::class.java)
            else
                ProfileModel()
        }.get(5, TimeUnit.SECONDS)
    } catch (e: Exception) {
        ProfileModel()
    }

    override suspend fun setProfile(userId: String, name: CharSequence?, email: CharSequence?, profile_url: String?): Boolean {
        return suspendCancellableCoroutine { continuation ->
            //            val userId = PP.user_id.get()
//            if (userId.isNullOrEmpty())
//                throw IllegalArgumentException("PP.user_id.get() is empty")

            val db = FirebaseFirestore.getInstance()
            val ref = db.collection(USER_COLLECTION).document(userId)
            val map = hashMapOf<String, Any>()
            name?.let { map[USER_DOCUMENT.NAME] = name }
            email?.let { map[USER_DOCUMENT.EMAIL] = email }
            profile_url?.let { map[USER_DOCUMENT.PROFILE_URL] = profile_url }
            val task = ref.update(map as Map<String, Any>)
            task.addOnCompleteListener {
                Log.e("addOnCompleteListener")
                if (it.isComplete && it.isSuccessful)
                    Log.i("addOnCompleteListener maybe success?")
                else
                    Log.w("addOnCompleteListener maybe fail?")
                Log.w("addOnCompleteListener")
            }

            task.addOnSuccessListener {
                Log.e("addOnSuccessListener")
                continuation.resume(true)
                Log.w("addOnSuccessListener")
            }
            task.addOnCanceledListener {
                Log.e("addOnCanceledListener")
                continuation.cancel()
                Log.w("addOnCanceledListener")
            }
            task.addOnFailureListener {
                Log.e("addOnFailureListener")
                continuation.resumeWithException(it)
                Log.w("addOnFailureListener", "continuation.resumeWithException(it)")
            }
            continuation.invokeOnCancellation {
                Log.e("continuation.invokeOnCancellation")
                continuation.cancel()
            }
        }
    }

    override suspend fun join(name: CharSequence, email: CharSequence): String {
        return suspendCancellableCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            val ref = db.collection(USER_COLLECTION)
            val task = ref.add(hashMapOf<String, Any>(USER_DOCUMENT.NAME to name.toString(), USER_DOCUMENT.EMAIL to email.toString()))
            task.addOnCompleteListener {
                Log.e("addOnCompleteListener")
                if (it.isComplete && it.isSuccessful)
                    Log.i("addOnCompleteListener maybe success?")
                else
                    Log.w("addOnCompleteListener maybe fail?")
                Log.w("addOnCompleteListener")
            }

            task.addOnSuccessListener {
                Log.e("addOnSuccessListener")
                continuation.resume(it.id)
                Log.w("addOnSuccessListener", it.id)
            }
            task.addOnCanceledListener {
                Log.e("addOnCanceledListener")
                continuation.cancel()
                Log.w("addOnCanceledListener")
            }
            task.addOnFailureListener {
                Log.e("addOnFailureListener")
                continuation.resumeWithException(it)
                Log.w("addOnFailureListener")
            }
            continuation.invokeOnCancellation {
                Log.e("continuation.invokeOnCancellation")
                continuation.cancel()
            }
        }
    }

//    override fun join(name: String, email: String, imageUrl: String?): Boolean {
//        FirebaseFirestore.getInstance().collection(USER_COLLECTION)
//                .add(profileModel)
//                .addOnSuccessListener { documentReference ->
//                    PP.user_id.set(documentReference.id)
//                    Log.e(PP.user_id.get(), "회원가입이 완료됨")
//                }
//                .addOnFailureListener { e -> e.printStackTrace() }
//        return true
//    }

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

    override suspend fun loadGalleryLoad(context: Context): String? {
        return suspendCancellableCoroutine { continuation ->
            try {
                GalleryLoader.builder(context)
                        .setCrop(true, 100.dp, 100.dp)
                        .setOnGalleryLoadedListener { uri ->
                            Log.e("continuation.setOnGalleryLoadedListener", uri)
                            continuation.resume(uri.toString())
                            Log.w("continuation.setOnGalleryLoadedListener")
                        }
                        .setOnCancelListener {
                            Log.e("continuation.setOnCancelListener")
                            continuation.cancel()
                            Log.w("continuation.setOnCancelListener")
                        }
                        .load()
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
            continuation.invokeOnCancellation {
                Log.e("continuation.invokeOnCancellation")
                continuation.resumeWithException(EmptyStackException())
            }
        }
    }

    override suspend fun uploadToFireStorage(user_id: String, stream: InputStream): String? {
        return suspendCancellableCoroutine { continuation ->
            var ref = FirebaseStorage.getInstance()
                    .reference
                    .child("profile/${user_id}")

            val task = ref.putStream(stream)

            task.addOnCompleteListener {
                Log.e("addOnCompleteListener")
                if (it.isComplete && it.isSuccessful)
                    Log.i("addOnCompleteListener maybe success?")
                else
                    Log.w("addOnCompleteListener maybe fail?")
                Log.w("addOnCompleteListener")
            }
            task.addOnSuccessListener {
                Log.e("addOnSuccessListener1")
                ref.downloadUrl.addOnSuccessListener {
                    Log.e("addOnSuccessListener2")
                    continuation.resume(it.toString())
                    Log.w("addOnSuccessListener2", it.toString())
                }
                Log.w("addOnSuccessListener1")
            }
            task.addOnCanceledListener {
                Log.e("addOnCanceledListener")
                continuation.cancel(EmptyStackException())
                Log.w("addOnCanceledListener")
            }
            task.addOnFailureListener {
                Log.e("addOnFailureListener")
                continuation.resumeWithException(it)
                Log.w("addOnFailureListener")
            }
            continuation.invokeOnCancellation {
                Log.e("continuation.invokeOnCancellation")
                continuation.resumeWithException(EmptyStackException())
            }
        }
    }
}