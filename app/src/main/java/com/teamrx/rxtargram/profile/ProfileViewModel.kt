package com.teamrx.rxtargram.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    fun setProfle() {
        val userId: String = ""
        val email: String = ""
        val name: String = ""
        val profile_url: String? = ""
        val profile = ProfileModel(userId, email, name, profile_url)
//        FirebaseFirestore.getInstance().collection(Const.USER).document().set(profile)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful)
//            }
        getTotalX(arrayOf(2,4 ),arrayOf(16,32,96 ))
    }

    fun getTotalX(a: Array<Int>, b: Array<Int>): Int {
        var result = ArrayList<Int>()
        var min = a.min()
        var max1 = a.max()
        var max = b.max()

        for (i in max1!! downTo max!! step min!!) {
            var isGood = true
            for (j in 0..b.size) {
                if (b[j] % i !== 0) {
                    isGood = false;
                    break;
                }
            }

            if (isGood) {
                for (j in 0..a.size) {
                    if (i % a[j] !== 0) {
                        isGood = false;
                        break;
                    }
                }
            }

            if (isGood) {
                result.add(i)
            }
        }
        return result.size
    }
}
