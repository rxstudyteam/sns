package com.teamrx.rxtargram.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.teamrx.rxtargram.R
import kotlinx.android.synthetic.main.fragment_email_login.*

class EmailLoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        login.setOnClickListener {
            if(FirebaseAuth.getInstance().currentUser == null)
                performLoginOrAccountCreation(edt_email.text.toString(), edt_password.text.toString())
        }

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
        }
    }

    private fun performLoginOrAccountCreation(email: String, password: String) {
        // 이메일/비밀번호와 이메일링크를 모두 사용하는 경우
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnSuccessListener { task ->
                val signInMethods  = task.signInMethods
                when {
                    signInMethods?.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)!! -> performLogin(email, password)
                    signInMethods.contains(EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD) -> performLingLogin(email)
                    else -> performLogin(email, password)
                }
            }
            .addOnFailureListener { e ->  e.printStackTrace() }
    }

    private fun performLingLogin(email: String) {

    }

    private fun performLogin(email: String, password: String) {
        // 신규 계정 생성 및 자동 로그인
        // 짧은 시간 동안 같은 IP 주소에서 이메일/비밀번호 및 익명 방식으로 애플리케이션에 새로 가입할 수 있는 횟수가 제한
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener  { task ->
                if(task.isSuccessful) {
                    println("로그인 성공 current User : ${FirebaseAuth.getInstance().currentUser}")
                } else {
                    // 이미 이메일 계정이 있는 경우 로그인 시도
                    performSignIn(email, password)
                }
            }
    }

    private fun performSignIn(email: String, password: String) {
        // 로그인 처리
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener  { task ->
                if(task.isSuccessful) {
                    println("로그인 성공 current User : ${FirebaseAuth.getInstance().currentUser}")
                }
            }
    }
}
