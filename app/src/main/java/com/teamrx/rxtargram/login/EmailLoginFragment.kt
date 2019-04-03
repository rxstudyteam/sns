package com.teamrx.rxtargram.login


import android.os.Bundle
import android.text.TextUtils
import android.util.disableButton
import android.util.enableButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView
import com.teamrx.rxtargram.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_email_login.*

class EmailLoginFragment : Fragment() {

    val disposable = CompositeDisposable()

    companion object {
        val PASSWORD_REGEX = """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#${'$'}%!\-_?&])(?=\S+${'$'}).{8,}""".toRegex()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_email_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        hideEmailError()
        hidePasswordError()

        observeAuthState()
        observeComponents()

        login.setOnClickListener {
            if(FirebaseAuth.getInstance().currentUser == null) {
                performLoginOrAccountCreation(edt_email.text.toString().trim(), edt_password.text.toString().trim())
            }
        }

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposable.dispose()
    }

    private fun observeAuthState() {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            if(auth.currentUser != null) {
                logout.enableButton()
                login.disableButton()
            } else {
                logout.disableButton()
            }
        }
    }

    private fun observeComponents() {
        val emailObservable = RxTextView.textChanges(edt_email)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { !TextUtils.isEmpty(it) }
            .map { inputText -> validateEmail(inputText.toString()) }

        disposable += emailObservable.subscribe ({ isEmailValid ->
            if(isEmailValid) hideEmailError() else showEmailError()
        }, { e -> e.printStackTrace() } )

        val passwordObservable = RxTextView.textChanges(edt_password)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { !TextUtils.isEmpty(it) }
            .map { inputText -> validatePassword(inputText.toString()) }

        disposable += passwordObservable.subscribe ({ isPasswordValid ->
            if(isPasswordValid) hidePasswordError() else showPasswordError()
        }, { e -> e.printStackTrace() } )

        disposable += Observables.combineLatest(emailObservable, passwordObservable) {
                isEmailValid, isPasswordValid -> isEmailValid && isPasswordValid }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({ valid ->
                if(valid) login.enableButton() else login.disableButton()
            }, { e -> e.printStackTrace() } )
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
                    Toast.makeText(requireContext(), "회원가입 및 로그인 성공", Toast.LENGTH_SHORT).show()
                    println("회원가입 및 로그인 성공 current User : ${FirebaseAuth.getInstance().currentUser}")
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
                    Toast.makeText(requireContext(), "로그인 성공", Toast.LENGTH_SHORT).show()
                    println("로그인 성공 current User : ${FirebaseAuth.getInstance().currentUser}")
                } else {
                    Toast.makeText(requireContext(), "로그인 실패 (ID 또는 PW 미일치)", Toast.LENGTH_SHORT).show()
                    println("로그인 실패 (ID 또는 PW 미일치)")
                }
            }
    }

    private fun enableError(textInputLayout: TextInputLayout) {
        if(textInputLayout.childCount == 2)
            textInputLayout.getChildAt(1).visibility = View.VISIBLE
    }

    private fun disableError(textInputLayout: TextInputLayout) {
        if(textInputLayout.childCount == 2)
            textInputLayout.getChildAt(1).visibility = View.GONE
    }

    private fun hideEmailError() {
        disableError(email_layout)
        email_layout.error = null
    }

    private fun hidePasswordError() {
        disableError(password_layout)
        password_layout.error = null
    }

    private fun showEmailError() {
        enableError(email_layout)
        email_layout.error = getString(R.string.invalid_email)
    }

    private fun showPasswordError() {
        enableError(password_layout)
        password_layout.error = getString(R.string.invalid_password)
    }

    private fun validateEmail(email: String): Boolean {
        if(TextUtils.isEmpty(email))
            return false

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        if(TextUtils.isEmpty(password))
            return false

        return password.matches(PASSWORD_REGEX)
    }
}
