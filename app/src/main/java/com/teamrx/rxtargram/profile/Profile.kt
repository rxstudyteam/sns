package com.teamrx.rxtargram.profile

import android.graphics.Bitmap
import android.log.Log
import android.os.Bundle
import android.util.check
import android.view.*
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
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

    companion object {
        val EXTRA_USER_ID = "user_id"
        fun newInstance(user_id: String? = null) = Profile().apply {
            arguments = Bundle().apply {
                putString(Profile.EXTRA_USER_ID, user_id)
            }
        }
    }

    private var user_id: String? = null

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

        arguments?.run {
            if (containsKey( EXTRA_USER_ID)) {
                user_id = getString(EXTRA_USER_ID)
            }
        }

        supportActionBar?.apply { title = vm.getTitle() }

        loadProfile(user_id)
    }

    private fun loadProfile(user_id: String?) = CoroutineScope(Dispatchers.Main).launch {
        showProgress()
        vm.updateProfile(user_id)
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

@BindingAdapter("load")
fun ImageView.load(imageUrl: String?) {
    Log.e(imageUrl)
    Log.e()
    Glide.with(this)
            .setDefaultRequestOptions(RequestOptions().apply {
                placeholder(R.drawable.ic_face_black_24dp)
                error(R.drawable.ic_face_black_24dp)
            })
            .asBitmap()
            .load(imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(object : BitmapImageViewTarget(this) {
                override fun setResource(resource: Bitmap?) {
                    resource?.let { bitmap ->
                        setTag(R.id.text, imageUrl)
                        setTag(R.id.icon, bitmap)
//                        Log.e(getTag(R.id.text))
//                        Log.e(getTag(R.id.icon))
                    }
                    super.setResource(resource)
                }
            })
}
