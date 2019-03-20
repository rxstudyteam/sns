package com.teamrx.rxtargram.login

import android.os.Bundle
import android.util.이가
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import com.teamrx.rxtargram.R

import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        kakao_login_button.setOnClickListener{

            Toast.makeText(this@LoginActivity, "카카오 로그인 여기다 붙이자", Toast.LENGTH_SHORT).show()

        }
    }

}
