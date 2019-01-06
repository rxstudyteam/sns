package android.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class CActivity : AppCompatActivity() {
    lateinit var mActivity: CActivity
    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
        mContext = this
    }
}