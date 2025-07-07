package com.example.reglab7firebase.data.repository

import com.example.reglab7firebase.data.model.InfoRepo
import com.example.reglab7firebase.data.model.Isi
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class InfoRepoImpl : InfoRepo{
    private val firestore = Firebase.firestore
    override fun getInformation(): Flow<List<Isi>> = callbackFlow{
        val listener = firestore.collection("informasi").addSnapshotListener {
            data,error->
            if (error!=null){
                close(error)
                return@addSnapshotListener
            }
            if (data!=null){
                val list = data.documents.mapNotNull {
                    val info = it.toObject(Isi::class.java)
                    info?.copy(uid = it.id)
                }
                trySend(list)
            }
        }
        awaitClose {
            listener.remove()
        }
    }
    override suspend fun getDetailInformation(uid: String): Isi?{
        try {
            val data = firestore.collection("informasi").document(uid).get().await()
            return data.toObject<Isi?>()
        }catch (e: Exception){
            throw e
        }
    }
    override suspend fun addInformation(datamasuk: Isi){
        try {
            val data = firestore.collection("informasi").document()
            datamasuk.copy(uid = data.id)
            data.set(datamasuk).await()
        }catch (e: Exception){
            throw e
        }
    }
    override suspend fun deleteInformation(id: String){
        try {
            val data = firestore.collection("informasi").document(id).delete().await()
        }catch (e: Exception){
            throw e
        }
    }
}