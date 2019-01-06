package android.etc

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

open class EastarEgg(activity: Activity) {
    val mActivity: Activity = activity

//    fun APPLICATION_DETAILS_SETTINGS() {
//        UU.setting(mActivity)
//    }
//
//    fun PUSH_TOKEN() {
//        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(mActivity) { instanceIdResult ->
//            val token = instanceIdResult.token
//            Log.e("FCM", token)
//            if (!UU.isEmpty(token)) {
//                val clipboard = mActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                val clip = ClipData.newPlainText("FCM", token)
//                clipboard.primaryClip = clip
//                Log.toast(mActivity, "복사되었습니다.")
//            }
//        }
//    }

}