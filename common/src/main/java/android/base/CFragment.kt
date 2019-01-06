package android.base

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment

open class CFragment : Fragment() {
    lateinit var mActivity: CActivity
    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = requireActivity() as CActivity
        mContext = requireContext()

    }
}