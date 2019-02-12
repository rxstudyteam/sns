@file:Suppress("unused")

package android.etc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.debug.AllActivity
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

    fun ACTION_CREDIT_CARD_OCR() {
        activity.startActivity(Intent.parseUri("intent:#Intent;action=com.google.android.gms.ocr.ACTION_CREDIT_CARD_OCR;end", Intent.URI_INTENT_SCHEME))
    }

    fun PROFILE_WRITER() {
        activity.startActivity(Intent(activity, Profile::class.java))
    }

    fun CoroutineFirestore2() {
        Log.w(1)
        runBlocking {
            Log.w(2)
            Log.e(3)
            var result = getProfile2()
            Log.w(4, result)
            Log.e(5, "to do your work")
        }
        Log.w(6)
    }

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
        return suspendCancellableCoroutine { continuation ->
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
                continuation.resume(it.toObject(ProfileModel::class.java)!!)
                Log.w("addOnSuccessListener", "continuation.resume(it.toObject(ProfileModel::class.java)!!)")
            }
            task.addOnCanceledListener {
                Log.e("addOnCanceledListener")
                continuation.cancel(EmptyStackException())
                Log.w("addOnCanceledListener", "continuation.cancel(EmptyStackException())")
            }
            task.addOnFailureListener {
                Log.e("addOnFailureListener")
                continuation.resumeWithException(it)
                Log.w("addOnFailureListener", "continuation.resumeWithException(it)")
            }
            continuation.invokeOnCancellation {
                Log.e("continuation.invokeOnCancellation")
            }
//                continuation.resumeWithException(EmptyStackException())
        }
    }

    //W/EastarEgg.CoroutineFirestore.......................(EastarEgg.kt:100): ``1
    //W/EastarEgg.CoroutineFirestore.......................(EastarEgg.kt:102): ``2
    //E/EastarEgg.CoroutineFirestore.......................(EastarEgg.kt:109): ``5,null
    //W/EastarEgg.CoroutineFirestore.......................(EastarEgg.kt:110): ``6
    //E/EastarEgg_CoroutineFirestore_job_1.invokeSuspend...(EastarEgg.kt:104): ``3
    //E/EastarEgg_getProfile2_2_1.onComplete...............(EastarEgg.kt:121): ``addOnCompleteListener
    //I/EastarEgg_getProfile2_2_1.onComplete...............(EastarEgg.kt:123): ``addOnCompleteListener maybe success?
    //W/EastarEgg_getProfile2_2_1.onComplete...............(EastarEgg.kt:126): ``addOnCompleteListener
    //E/EastarEgg_getProfile2_2_2.onSuccess................(EastarEgg.kt:130): ``addOnSuccessListener
    //W/EastarEgg_getProfile2_2_2.onSuccess................(EastarEgg.kt:132): ``addOnSuccessListener,cancellableContinuation.resume(it.toObject(ProfileModel::class.java)!!)
    //W/EastarEgg_CoroutineFirestore_job_1.invokeSuspend...(EastarEgg.kt:106): ``4,ProfileModel(user_id=KxUypfZKf2cKmJs4jOeU, name=eastar Jeong, email=eastarj@gmail.com, profile_url=https://firebasestorage.googleapis.com/v0/b/rxteam-sns.appspot.com/o/profile%2Favatar.png?alt=media&token=5d68ae9e-34e4-4b7c-a32d-32577ce944af)
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