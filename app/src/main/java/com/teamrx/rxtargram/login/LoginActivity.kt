package com.teamrx.rxtargram.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.util.exception.KakaoException
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.api.Auth
import com.teamrx.rxtargram.api.AuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    val TAG = LoginActivity::class.java.simpleName

    private val kakaoSessionCallback: KakaoSessionCallback = KakaoSessionCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Session.getCurrentSession().addCallback(kakaoSessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();

        kakao_login_button.setOnClickListener {
            Session.getCurrentSession().open(AuthType.KAKAO_TALK, this)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(kakaoSessionCallback)
    }

    private inner class KakaoSessionCallback : ISessionCallback {
        override fun onSessionOpened() {
            val accessToken = Session.getCurrentSession().tokenInfo.accessToken
            Log.e(TAG, "kakao accessToekn $accessToken")

            AuthProvider.verifyToken(accessToken).enqueue(object : Callback<Auth> {
                override fun onFailure(call: Call<Auth>, t: Throwable) {
                    Toast.makeText(applicationContext, "Failed to create a Firebase user.", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "verifyToken e: $t")
                }

                override fun onResponse(call: Call<Auth>, response: Response<Auth>) {


                    val firebaseToken: String? = response.body()?.firebase_token
                    if (firebaseToken != null) {
                        loginFireBase(firebaseToken)
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login Fail token is null",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }

        override fun onSessionOpenFailed(e: KakaoException) {

        }
    }

    fun loginFireBase(accessToken: String) {
        val auth = FirebaseAuth.getInstance()
        val task = auth.signInWithCustomToken(accessToken)

        task.addOnCompleteListener(object : OnCompleteListener<AuthResult> {
            override fun onComplete(p0: Task<AuthResult>) {
                if (task.isSuccessful()) {
                    Toast.makeText(
                        this@LoginActivity,
                        "로그인 성공 ${Session.getCurrentSession().tokenInfo.accessToken}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to create a Firebase user.", Toast.LENGTH_LONG)
                        .show();
                    if (task.getException() != null) {
                        Log.e(TAG, task.getException().toString());
                    }
                }
            }
        })


    }
}

