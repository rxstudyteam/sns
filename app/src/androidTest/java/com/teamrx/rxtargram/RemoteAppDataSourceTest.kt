package com.teamrx.rxtargram

import android.log.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.teamrx.rxtargram.model.ProfileModel
import com.teamrx.rxtargram.repository.RemoteAppDataSource
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class RemoteAppDataSource {
    @Test
    suspend fun getProfile() {
        Log.MODE = Log.eMODE.SYSTEMOUT
        var user_id = "KxUypfZKf2cKmJs4jOeU"
        val pm = RemoteAppDataSource.getProfile(user_id)
        Log.e(pm)
        Log.e(pm == ProfileModel())
        Log.e(pm is ProfileModel)
        Assert.assertTrue(pm is ProfileModel)
        Assert.assertTrue(pm == ProfileModel("eastar Jeong", "eastarj@gmail.com", "https://firebasestorage.googleapis.com/v0/b/rxteam-sns.appspot.com/o/profile%2Favatar.png?alt=media&token=5d68ae9e-34e4-4b7c-a32d-32577ce944af"))
    }


    @Test
    suspend fun joinableFromPhone(phoneNumber: CharSequence){
        // 해당 전화 번호로 가입이 가능한지 체크한다
        val result = RemoteAppDataSource.joinableFromPhone("01074950523")
        Assert.assertTrue(result)
    }
}
