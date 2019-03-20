package com.teamrx.rxtargram.login

import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Bundle
import android.util.이가
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import com.kakao.auth.AuthType
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.util.exception.KakaoException
import com.teamrx.rxtargram.R

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

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
            Session.getCurrentSession().tokenInfo.accessToken
            Toast.makeText(
                this@LoginActivity,
                "로그인 성공 ${Session.getCurrentSession().tokenInfo.accessToken}",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onSessionOpenFailed(e: KakaoException) {

        }
    }

}
