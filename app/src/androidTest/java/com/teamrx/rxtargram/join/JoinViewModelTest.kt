package com.teamrx.rxtargram.join

import android.app.Activity
import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class JoinViewModelTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
    }


    @Test
    fun testJoinWithPhone() {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential?) {
                println("onVerificationCompleted:$credential")
                assert(true)
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
                println(failMessage)
            }

        }


        val phoneNumber = "+821012341234"

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60L, // Timeout  duration
            TimeUnit.SECONDS,
            context as Activity,
            callbacks
        )
    }
}