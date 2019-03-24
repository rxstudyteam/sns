package com.teamrx.rxtargram.join

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.teamrx.rxtargram.BuildConfig
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity
import kotlinx.android.synthetic.main.activity_join.*

/**
 *
 * https://firebase.google.com/docs/auth/android/phone-auth?hl=ko
 *
 * Created by Rell on 2019. 3. 8..
 * 회원 가입 액티비티
 */
class JoinActivity : AppActivity() {

    private lateinit var viewModel: JoinViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        setupViewModel()
        setupButtonsEvents()
        setupDebug()
    }

    private fun setupDebug() {
        if (BuildConfig.DEBUG) {
            editInsertPhone.setText("+821012341234")
            editInsertSmsCode.setText("123456")
        }
    }

    private fun setupViewModel() {
        viewModel = getViewModel()

        viewModel.observeJoinResult().observe(this, Observer { result ->
            Toast.makeText(this@JoinActivity, result.second, Toast.LENGTH_SHORT).show()
            if (result.first) {

            } else {

            }
        })

        viewModel.observeJoinStatus().observe(this, Observer { status ->
            when (status) {
                JoinStatus.ENABLE_SMS_INPUT_CODE -> showInputSmsLayout()
            }
        })

        viewModel.observeToast().observe(this, Observer { msg ->
            toast(msg)
        })
    }

    private fun showInputSmsLayout() {
        layoutInputSms.visibility = View.VISIBLE
    }

    private fun setupButtonsEvents() {
        buttonJoin.setOnClickListener {
            val phoneNumber = editInsertPhone.text.toString()
            viewModel.joinWithPhone(this@JoinActivity, phoneNumber)
        }

        sumitSmsCode.setOnClickListener {
            val smsNumber = editInsertSmsCode.text.toString()
            viewModel.inputSmsCode(smsNumber)
        }
    }

}