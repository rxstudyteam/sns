package android.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment

open class CFragment : Fragment() {
    lateinit var mActivity: CActivity
    lateinit var mContext: Context
    var supportActionBar: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = requireActivity() as CActivity
        mContext = requireContext()
        supportActionBar = mActivity.supportActionBar
    }

    fun showProgress() = (requireActivity() as CActivity).showProgress()
    fun dismissProgress() = mActivity.dismissProgress()
    fun dismissProgressForce() = mActivity.dismissProgressForce()
}