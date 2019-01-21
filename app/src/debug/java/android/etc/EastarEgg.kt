@file:Suppress("unused")

package android.etc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.log.Log
import android.net.Uri
import android.provider.Settings
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.teamrx.rxtargram.model.ProfileModel
import com.teamrx.rxtargram.profile.Profile
import com.teamrx.rxtargram.repository.RemoteAppDataSource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import smart.base.AllActivity
import smart.util.GalleryLoader
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("FunctionName")
@SuppressLint("CheckResult")
class EastarEgg(val activity: Activity) {
    fun APPLICATION_DETAILS_SETTINGS() = activity.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.packageName)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    fun ALL_ACTIVITY() = activity.startActivity(Intent(activity, AllActivity::class.java))

    fun PUSH_TOKEN() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(activity) { instanceIdResult ->
            val token = instanceIdResult.token
            Log.e("FCM", token)
            if (!token.isEmpty()) {
                val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("FCM", token)
                clipboard.primaryClip = clip
                Log.toast(activity, "복사되었습니다.")
            }
        }
    }

    fun GALLERY_LOADER_BY_CAMERA() {
        GalleryLoader.builder(activity)
//                .setCrop(true, 100, 100)
                .setSource(GalleryLoader.Source.CAMERA)
                .setOnGalleryLoadedListener(this::showToast)
                .setOnCancelListener { Log.toast(activity, "canceled") }
                .load()
    }

    fun GALLERY_LOADER_BY_GALLERY() {
        GalleryLoader.builder(activity)
//                .setCrop(true, 100, 100)
                .setSource(GalleryLoader.Source.GALLERY)
                .setOnGalleryLoadedListener(this::showToast)
                .setOnCancelListener { Log.toast(activity, "canceled") }
                .load()
    }

    fun GALLERY_LOADER_BY_CAMERA_CROP() {
        GalleryLoader.builder(activity)
                .setCrop(true, 100, 100)
                .setSource(GalleryLoader.Source.CAMERA)
                .setOnGalleryLoadedListener(this::showToast)
                .setOnCancelListener { Log.toast(activity, "canceled") }
                .load()
    }

    fun GALLERY_LOADER_BY_GALLERY_CROP() {
        GalleryLoader.builder(activity)
                .setCrop(true, 100, 100)
                .setSource(GalleryLoader.Source.GALLERY)
                .setOnGalleryLoadedListener(this::showToast)
                .setOnCancelListener { Log.toast(activity, "canceled") }
                .load()
    }

    fun GALLERY_LOADER_BY_SELECT_CROP() {
        GalleryLoader.builder(activity)
                .setCrop(true, 100, 100)
//                .setSource(GalleryLoader.eSource.GALLERY)
                .setOnGalleryLoadedListener(this::showToast)
                .setOnCancelListener { Log.toast(activity, "canceled") }
                .load()
    }

    fun PROFILE_WRITER() {
        activity.startActivity(Intent(activity, Profile::class.java))
    }

    fun AAAAAAAAAAAA() {
//        RemoteAppDataSource.getProfile("KxUypfZKf2cKmJs4jOeU")
//        RemoteAppDataSource.getProfile("")
//        CoroutineScope(activity.lifecycle)<ProfileModel?> { getProfile2(). }
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
        return suspendCancellableCoroutine<ProfileModel?> { cancellableContinuation: CancellableContinuation<ProfileModel?> ->
            val task = FirebaseFirestore.getInstance().collection(RemoteAppDataSource.USER_COLLECTION).document("KxUypfZKf2cKmJs4jOeU").get()
            task.addOnCompleteListener {
                Log.e("addOnCompleteListener")
                if (it.isComplete && it.isSuccessful && it.result?.exists()!!) else cancellableContinuation.resumeWithException(EmptyStackException())
                Log.w("addOnCompleteListener")
            }
            task.addOnSuccessListener {
                Log.w("addOnCanceledListener")
                cancellableContinuation.resume(it.toObject(ProfileModel::class.java)!!)
            }
            task.addOnCanceledListener {
                Log.e("addOnCanceledListener")
                cancellableContinuation.cancel(EmptyStackException())
            }
            task.addOnFailureListener {
                Log.e("addOnFailureListener")
                cancellableContinuation.resumeWithException(it)
            }
            cancellableContinuation.invokeOnCancellation {
                Log.e("invokeOnCancellation")
            }
        }
    }

    private fun showToast(uri: Uri) {
        Log.toast(activity, uri.toString())
        Observable.fromFuture(Glide.with(activity).asBitmap().load(uri).submit())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { bitmap ->
                    Toast(activity).apply {
                        val iv = ImageView(activity)
                        iv.setImageBitmap(bitmap)
                        view = iv
                        show()
                    }
                }
    }
}