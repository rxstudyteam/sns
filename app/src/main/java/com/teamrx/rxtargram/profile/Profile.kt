package com.teamrx.rxtargram.profile

import android.graphics.Bitmap
import android.os.Bundle
import android.util.check
import android.view.*
import androidx.lifecycle.ViewModelProviders
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppFragment
import com.teamrx.rxtargram.databinding.ProfileWriteBinding
import com.teamrx.rxtargram.inject.Injection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Profile : AppFragment() {
    private lateinit var bb: ProfileWriteBinding
    private lateinit var vm: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = ProfileWriteBinding.inflate(inflater).also { bb = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProviders.of(mActivity, Injection.provideViewModelFactory()).get(ProfileViewModel::class.java)
        bb.apply {
            profileViewModel = vm
            lifecycleOwner = mActivity
        }

        supportActionBar?.apply { title = vm.getTitle() }

        loadProfile()
    }

    private fun loadProfile() = CoroutineScope(Dispatchers.Main).launch {
        showProgress()
        vm.updateProfile()
        dismissProgress()
    }

    private fun saveProfile() = CoroutineScope(Dispatchers.Main).launch {
        if (check()) {
            showProgress()

            vm.saveProfile(bb.name.text.toString()
                    , bb.email.text.toString()
                    , bb.profileUrl.getTag(R.id.uri) as String to bb.profileUrl.getTag(R.id.bitmap) as Bitmap)

            supportActionBar?.apply { title = vm.getTitle() }

            dismissProgress()
        }
    }

    private fun check() = bb.name.check() && bb.email.check()

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.save -> saveProfile().let { true }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
