package com.teamrx.rxtargram.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamrx.rxtargram.model.PostDTO
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Test

/**
 * Created by Rell on 2019. 1. 30..
 */
class RemoteAppDataSourceTest {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    @Test
    fun modifyPost() {
        println("setPost()")

        val docRef = firestore.collection(RemoteAppDataSource.POST_COLLECTION).document("FyJRvSzqOLOl6nENqFFV").get()


        Single.just(docRef)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map { docRef.result }
                .doOnSuccess { println("setPost") }
                .test()
                .await()




        docRef.addOnSuccessListener {
            val post = it.toObject(PostDTO::class.java)

            println("modifyPost : $post")
        }

        Thread.sleep(2000)

    }

    @Test
    fun testUpdate() {
        println("testUpdate()")


        val docRef = firestore.collection(RemoteAppDataSource.POST_COLLECTION).document("FyJRvSzqOLOl6nENqFFV")

        docRef.update("content", "변경2")


//        Single.just(firestore.collection(RemoteAppDataSource.POST_COLLECTION).document("FyJRvSzqOLOl6nENqFFV"))
//                .map { it.update("content", "변경2") }
//                .map { it.result }
//                .test().await()



//        firestore.collection(RemoteAppDataSource.POST_COLLECTION).document("FyJRvSzqOLOl6nENqFFV").update("content", "변경")
//                .addOnSuccessListener {
//                    println(it.toString())
//                }

//        Thread.sleep(2000)
    }


    @Test
    fun SignUpWithEmailAndPassword() {
        auth.createUserWithEmailAndPassword("rlawlgns077@naver.com", "test1234")
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    println("current User : ${FirebaseAuth.getInstance().currentUser}")
                } else {
                    println("실패")
                }
            }

        Thread.sleep(10000)
    }

}