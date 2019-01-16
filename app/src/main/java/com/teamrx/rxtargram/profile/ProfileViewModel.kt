package com.teamrx.rxtargram.profile

import android.app.Application
import android.log.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.firestore.FirebaseFirestore
import smart.base.PP

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val user_collection = "user"
    }

    //가입
    fun join(email: String, name: String, profile_url: String? = null) {
        FirebaseFirestore.getInstance().collection(user_collection)
                .add(ProfileModel(email, name, profile_url))
                .addOnSuccessListener { documentReference ->
                    PP.user_id.set(documentReference.id)
                    Log.e(PP.user_id.get())
                }
                .addOnFailureListener { e -> e.printStackTrace() }
    }

    fun getProfile() {
        FirebaseFirestore.getInstance()
                .collection(user_collection).document(PP.user_id.get())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var document = task.result!!
                        Log.d(document.id, document.data)
                    } else {
                        Log.w(task.exception)
                    }
                }
                .addOnFailureListener { e -> e.printStackTrace() }

    }

    fun getProfle() {
        //목록
        FirebaseFirestore.getInstance()
                .collection(user_collection)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            Log.d(document.id, document.data)
                        }
                    } else {
                        Log.w(task.exception)
                    }
                }
                .addOnFailureListener { e -> e.printStackTrace() }

//        val database = FirebaseDatabase.getInstance()
//        val ref = database.getReference().g
//        ref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // This method is called once with the initial value and again whenever data at this location is updated.
//                val value = dataSnapshot.getValue(String::class.java)
//                Log.e(value)
//            }
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.e(error)
//            }
//        })
    }

}
