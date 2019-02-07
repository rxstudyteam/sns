package com.teamrx.rxtargram.profile

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.log.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamrx.rxtargram.model.ProfileModel
import com.teamrx.rxtargram.repository.AppDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import smart.base.PP
import java.io.ByteArrayOutputStream

class ProfileViewModel(private var dataSource: AppDataSource) : ViewModel() {
    lateinit var profileModel: MutableLiveData<ProfileModel>
    fun getProfile(): LiveData<ProfileModel> {
        Log.e(1)
        if (!::profileModel.isInitialized) {
            Log.e(2)
            profileModel = MutableLiveData()
            val userId = PP.user_id.get("")!!
            profileModel.value = dataSource.getProfile(userId)
        }
        Log.e(profileModel.value)
        return profileModel
    }

    fun saveProfile(name: String?, email: String?, imageUrl: String?): Boolean {
        val userId = PP.user_id.get()
        return if (userId.isNullOrEmpty()) {
            Log.w("join")
            try {
                dataSource.join(name!!, email!!, imageUrl)
            } catch (e: Exception) {
                false
            }
        } else {
            Log.w("update")
            try {
                dataSource.setProfile(name, email, imageUrl)
            } catch (e: Exception) {
                false
            }
        }
    }

    fun imageView2bitmap(imageView: ImageView): ByteArray? {
        // Get the data from an ImageView as bytes
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        return data
    }

    fun getTitle() = if (PP.user_id.get().isNullOrBlank()) "회원가입" else "프로필"

    fun getUserImage(view: View) {
        Log.e(0, "getUserImage")
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(1, "getUserImage")
            val context = view.context
            Log.e(2, "getUserImage")
            var profile_url = dataSource.loadGalleryLoad(context)
            Log.w(2, "getUserImage")
//            Log.e(3, "getUserImage")
//            profile_url = dataSource.uploadToFireStorage(context, profile_url)
//            Log.w(3, "getUserImage")
            profile_url?.run {
                Log.e(4, "getUserImage")
                profileModel.value = profileModel.value?.copy(profile_url = profile_url) ?: ProfileModel(profile_url = profile_url)
                Log.w(4, "getUserImage")
            }
            Log.w(1, "getUserImage")
        }
        Log.w(99, "getUserImage")
    }
}
/*

    fun CoroutineFirestore() {
        Log.w(1)
        var result: ProfileModel? = null
        Log.w(2)
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(3)
            result = getProfile2()
            Log.w(4, result)
        }

        Log.e(5, result)
        Log.w(6)
    }

    suspend fun getProfile2(): ProfileModel? {
        return suspendCancellableCoroutine { cancellableContinuation: CancellableContinuation<ProfileModel?> ->
            val task = FirebaseFirestore.getInstance()
                    .collection(RemoteAppDataSource.USER_COLLECTION)
                    .document("KxUypfZKf2cKmJs4jOeU")
                    .get()

            task.addOnCompleteListener {
                Log.e("addOnCompleteListener")
                if (it.isComplete && it.isSuccessful && it.result?.exists()!!)
                    Log.i("addOnCompleteListener maybe success?")
                else
                    Log.w("addOnCompleteListener maybe fail?")
                Log.w("addOnCompleteListener")
            }

            task.addOnSuccessListener {
                Log.e("addOnSuccessListener")
                cancellableContinuation.resume(it.toObject(ProfileModel::class.java)!!)
                Log.w("addOnSuccessListener", "cancellableContinuation.resume(it.toObject(ProfileModel::class.java)!!)")
            }
            task.addOnCanceledListener {
                Log.e("addOnCanceledListener")
                cancellableContinuation.cancel(EmptyStackException())
                Log.w("addOnCanceledListener", "cancellableContinuation.cancel(EmptyStackException())")
            }
            task.addOnFailureListener {
                Log.e("addOnFailureListener")
                cancellableContinuation.resumeWithException(it)
                Log.w("addOnFailureListener", "cancellableContinuation.resumeWithException(it)")
            }
            cancellableContinuation.invokeOnCancellation {
                Log.e("cancellableContinuation.invokeOnCancellation")
            }
//                cancellableContinuation.resumeWithException(EmptyStackException())
        }
    }
 */
