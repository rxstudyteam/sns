package com.teamrx.rxtargram.profile

import android.graphics.Bitmap
import android.log.Log
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity
import com.teamrx.rxtargram.databinding.ProfileWriteBinding
import com.teamrx.rxtargram.inject.Injection
import smart.util.check

class Profile : AppActivity() {
    private lateinit var bb: ProfileWriteBinding
    private lateinit var vm: ProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProviders.of(mActivity, Injection.provideViewModelFactory()).get(ProfileViewModel::class.java)
        val binding = DataBindingUtil.setContentView<ProfileWriteBinding>(mActivity, R.layout.profile_write)
        bb = binding.apply {
            profileViewModel = vm
            lifecycleOwner = mActivity
        }

        supportActionBar?.apply {
            title = vm.getTitle()
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        vm.updateProfile()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> saveProfile()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun saveProfile() {
        if (!check())
            return

        bb.profileViewModel!!.saveProfile(bb.name.text.toString(), bb.email.text.toString(), bb.profileUrl.getTag(R.id.text) as String?, bb.profileUrl.getTag(R.id.icon) as Bitmap?)
//        if (bb.profileViewModel!!.saveProfile(bb.name.text, bb.email.text, bb.profileUrl.getTag(R.id.icon) as Bitmap)) {
//            Toast.makeText(this, "변경됨", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun check(): Boolean {
        if (!bb.name.check()) return false
        if (!bb.email.check()) return false
        return true
    }
}

@BindingAdapter("load")
fun ImageView.load(imageUrl: String?) {
    Log.e(imageUrl)
    Glide.with(this)
            .setDefaultRequestOptions(RequestOptions().apply { error(R.drawable.ic_face_black_24dp) })
            .asBitmap()
            .load(imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(object : BitmapImageViewTarget(this) {
                override fun setResource(resource: Bitmap?) {
                    resource?.let { bitmap ->
                        setTag(R.id.text, imageUrl)
                        setTag(R.id.icon, bitmap)
                        Log.e(getTag(R.id.text))
                        Log.e(getTag(R.id.icon))
                    }
                    super.setResource(resource)
                }
            })
}
