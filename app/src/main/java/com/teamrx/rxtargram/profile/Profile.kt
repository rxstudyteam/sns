package com.teamrx.rxtargram.profile

import android.log.Log
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.teamrx.rxtargram.R
import com.teamrx.rxtargram.base.AppActivity
import com.teamrx.rxtargram.databinding.ProfileWriteBinding
import com.teamrx.rxtargram.inject.Injection

class Profile : AppActivity() {
    private lateinit var bb: ProfileWriteBinding
    private lateinit var vm: ProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProviders.of(mActivity, Injection.provideViewModelFactory()).get(ProfileViewModel::class.java)
        val binding = DataBindingUtil.setContentView<ProfileWriteBinding>(mActivity, R.layout.profile_write)
        bb = binding.apply {
            profileViewModel = vm
            setLifecycleOwner(mActivity)
        }

        supportActionBar?.apply {
            title = vm.getTitle()
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
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
        if (bb.profileViewModel!!.saveProfile(bb.name.text.toString(), bb.email.text.toString(), bb.profileUrl.tag as String?)) {
            Toast.makeText(this, "변경됨", Toast.LENGTH_SHORT).show()
        }
    }
}

@BindingAdapter("bind:load")
fun ImageView.load(imageUrl: String?) {
    Log.e(imageUrl)
    Glide.with(this)
            .setDefaultRequestOptions(RequestOptions().apply {
                //            placeholder(R.drawable.ic_face_black_24dp)
                error(R.drawable.ic_face_black_24dp)
            })
            .load(imageUrl)
//            .load(  "content://com.teamrx.rxtargram.provider/gloader/result_20190130_174417_8270220685296630039.jpg")
            .apply(RequestOptions.circleCropTransform())
            .into(this)
}
