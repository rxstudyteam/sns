package com.teamrx.rxtargram.join

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
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
    private lateinit var joinResult: MutableLiveData<Pair<Boolean, String>>
    private lateinit var joinStatus: MutableLiveData<JoinStatus>
    private lateinit var toast: MutableLiveData<String>

    private val phoneAuthProvider = PhoneAuthProvider.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var verficationId: String? = null
    private lateinit var activity: Activity


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

        override fun onCodeSent(verficationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(verficationId, token)
            println("onCodeSent(verficationId : $verficationId, token : $token)")

            this@JoinViewModel.verficationId = verficationId
            joinStatus.postValue(JoinStatus.ENABLE_SMS_INPUT_CODE)

        }
    }

    fun joinWithPhone(activity: Activity, phoneNumber: String) {
        this.activity = activity
        phoneAuthProvider.verifyPhoneNumber(
                phoneNumber,
                60L, // Timeout  duration
                TimeUnit.SECONDS,
                activity,
                callbacks
        )
    }

    fun observeJoinResult(): LiveData<Pair<Boolean, String>> {
        if (!::joinResult.isInitialized) {
            joinResult = MutableLiveData()
        }

        return joinResult
    }

    fun observeJoinStatus(): LiveData<JoinStatus> {
        if (!::joinStatus.isInitialized) {
            joinStatus = MutableLiveData()
        }
        return joinStatus
    }

    fun observeToast(): LiveData<String> {
        if (!::toast.isInitialized) toast = MutableLiveData()

        return toast
    }

    fun inputSmsCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verficationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        println("signInWithCredential:success")

                        val user = task.result?.user
                        println("user : $user")
                        user?.let {
                            println("user.uid : ${it.uid}")
                            println("user.displayName : ${it.displayName}")
                            println("user.email : ${it.email}")
                            println("user.phoneNumber : ${it.phoneNumber}")

                        }
                    } else {
                        // Sign in failed, display a message and update the UI
                        println("signInWithCredential:failure ${task.exception}")
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            // SMS 인증코드가 유효하지 않습니다. SMS를 다시 보내고 사용자가 제공한 확인 코드를 사용하십시오.
                            toast.postValue("SMS 인증코드가 유효하지 않습니다. SMS를 다시 보내고 사용자가 제공한 확인 코드를 사용하십시오.")
                        }
                    }
                }
    }

}

enum class JoinStatus {
    ENABLE_SMS_INPUT_CODE
}