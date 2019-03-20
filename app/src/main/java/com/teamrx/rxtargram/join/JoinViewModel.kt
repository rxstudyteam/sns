package com.teamrx.rxtargram.join

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.teamrx.rxtargram.base.BaseViewModel
import com.teamrx.rxtargram.repository.AppDataSource
import java.util.concurrent.TimeUnit

/**
 *
 * 전화번호는 E.164 포맷을 준수해야 한다. (http://www.ktword.co.kr/abbr_view.php?m_temp1=1970)
 *
 */
class JoinViewModel(dataSource: AppDataSource) : BaseViewModel(dataSource) {
    lateinit var joinResult: MutableLiveData<Pair<Boolean, String>>


    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential?) {
            println("onVerificationCompleted:$credential")
            joinResult.postValue(Pair(true, "로그인에 성공하였습니다."))
        }

        override fun onVerificationFailed(e: FirebaseException?) {
            println("onVerificationFailed : ${e?.message}")

            var failMessage = ""

            if (e is FirebaseAuthInvalidCredentialsException) {
                // invalid request
                failMessage = "전화번호가 올바르지 않습니다."
            } else if (e is FirebaseTooManyRequestsException) {
                // the sms quota for the project has been exceed
                failMessage = "파이어베이스에 많은 요청으로 인증에  실패하였습니다."
            }
            joinResult.postValue(Pair(true, failMessage))
        }

    }

    fun joinWithPhone(activity: Activity, phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60L, // Timeout  duration
                TimeUnit.SECONDS,
                activity,
                callbacks)
    }

    fun observeJoinResult(): LiveData<Pair<Boolean, String>> {
        if (!::joinResult.isInitialized) {
            joinResult = MutableLiveData()
        }

        return joinResult
    }

}