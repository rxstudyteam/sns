package android.base

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.Lifecycle

open class CActivity : AppCompatActivity() {
    lateinit var mActivity: CActivity
    lateinit var mContext: Context
    private var destroied: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
        mContext = this
    }

    override fun onDestroy() {
        super.onDestroy()
        destroied = true
    }

    private val mProgress by lazy { createProgress() }

    open fun createProgress(): AppCompatDialog {
//        return AlertDialog.Builder(this).run {
        return with(AlertDialog.Builder(this)) {
            setCancelable(true)
            setView(ProgressBar(context, null, android.R.attr.progressBarStyleLarge))
            create().apply {
                window?.setBackgroundDrawable(ColorDrawable(0x00ff0000))
                setCanceledOnTouchOutside(false)
            }
        }
    }

    fun showProgress() {
        if (lifecycle.currentState === Lifecycle.State.DESTROYED) return
        if (isFinishing) return
        if (destroied) return

        mProgress.takeUnless { mProgress.isShowing }?.show()
    }

    fun dismissProgress() {
        mProgress.takeIf { mProgress.isShowing }?.dismiss()
    }

    fun dismissProgressForce() {
        dismissProgress()
    }

}