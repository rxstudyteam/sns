package com.teamrx.rxtargram.util

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun <T> CollectionReference.await(clazz: Class<T>): List<T> {
    return suspendCancellableCoroutine { continuation ->
        addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            try {
                if (querySnapshot == null) return@addSnapshotListener

                val list = arrayListOf<T>()
                for(snapshot in querySnapshot.documents) {
                    val item = snapshot.toObject(clazz)
                    item?.let { list.add(it) }
                }

                continuation.resume(list)

            } catch (e : FirebaseFirestoreException) {
                e.printStackTrace()
            }
        }
    }
}