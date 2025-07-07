package com.example.reglab7firebase.data.repository

import com.example.reglab7firebase.data.model.TimeRepo
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.functions.functions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TimeRepoImpl : TimeRepo {
    private val firestore = Firebase.firestore
    override suspend fun fetchServerTimeViaDummyDoc(uiduser: String): Timestamp {
        val dummyDocRef = firestore.collection("LogPresensi").document(uiduser)
        return suspendCoroutine { continuation ->
            dummyDocRef.set(mapOf("ts" to FieldValue.serverTimestamp()))
                .addOnSuccessListener {
                    dummyDocRef.get()
                        .addOnSuccessListener { documentSnapshot ->
                            val serverTimestamp = documentSnapshot.getTimestamp("ts")
                            if (serverTimestamp != null) {
                                continuation.resume(serverTimestamp)
                            } else {
                                continuation.resumeWithException(Exception("Timestamp null setelah dibaca dari server."))
                            }
                            dummyDocRef.delete()
                        }
                        .addOnFailureListener { readException ->
                            continuation.resumeWithException(readException)
                            dummyDocRef.delete()
                        }
                }
                .addOnFailureListener { writeException ->
                    continuation.resumeWithException(writeException)
                }
        }
    }
}