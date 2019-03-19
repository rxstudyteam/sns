package com.teamrx.rxtargram.profile

import android.graphics.Bitmap
import android.log.Log
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
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
import smart.util.check

class Profile : AppFragment() {
    private lateinit var bb: ProfileWriteBinding
    private lateinit var vm: ProfileViewModel

    companion object {
        fun newInstance(user_id: String? = null) = Profile().apply {
            val arg = Bundle()
            arg.putString("userID", user_id)
            arguments = arg
        }
    }

    private var user_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<ProfileWriteBinding>(inflater, R.layout.profile_write, container, false)
            .let { binding ->
                bb = binding
                binding.root
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProviders.of(mActivity, Injection.provideViewModelFactory()).get(ProfileViewModel::class.java)
        bb.apply {
            profileViewModel = vm
            lifecycleOwner = mActivity
        }

        arguments?.run {
            if (containsKey("userID")) {
                user_id = getString("userID")
            }
        }

        supportActionBar?.apply { title = vm.getTitle() }

        loadProfile(user_id)
    }


    private fun loadProfile(userId: String?) = CoroutineScope(Dispatchers.Main).launch {
        showProgress()
        vm.updateProfile(userId)
        dismissProgress()
    }

    private fun saveProfile() = CoroutineScope(Dispatchers.Main).launch {
        if (check()) {
            showProgress()
            vm.saveProfile(
                bb.name.text.toString()
                , bb.email.text.toString()
                , bb.profileUrl.getTag(R.id.text) as String?
                , bb.profileUrl.getTag(R.id.icon) as Bitmap?
            )

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
