package com.example.reglab7firebase.data.repository

import android.util.Log
import com.example.reglab7firebase.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await


interface UserRepo {

    fun cekUser(): Boolean

    fun cekCurent(): String

    suspend fun Register(email: String, password: String, nim: String): FirebaseUser

    fun Logout()

    suspend fun login(email: String, password: String): FirebaseUser

    suspend fun getUser(uid: String): User?

    suspend fun cekNimUser(nim: String): List<User?>?

    fun getPeoplePraktikum(uids: List<String>): Flow<List<User>>

    fun searchMahasiswa(query: String): Flow<List<User>>
}


class ImplementasiUserRepo: UserRepo {
    private val firestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth

    override fun cekUser(): Boolean{
        return auth.currentUser!=null
    }
    override fun cekCurent(): String {
        return auth.currentUser?.uid ?: "tidak ada user"
    }
    override suspend fun Register(email: String, password: String, nim: String): FirebaseUser {
        try {
            val user = auth.createUserWithEmailAndPassword(email, password).await()
            val detailuser = user.user ?: throw IllegalStateException("Gagal user null")
            val newUser = User(uid = detailuser.uid, email = email, password = password, nim = nim)
            firestore.collection("users").document(detailuser.uid).set(newUser).await()
            return detailuser

        } catch (e: Exception) {
            Log.d("error", e.toString())
            throw e
        }
    }
    override fun Logout(){
        auth.signOut()
    }
    override suspend fun login(email: String, password: String): FirebaseUser{
        try {
            auth.signInWithEmailAndPassword(email,password).await()
            val user = auth.currentUser?:throw IllegalStateException("tidak ada yang login")
            return user
        }catch (e: Exception){
            throw e
        }
    }
    override suspend fun getUser(uid: String): User? {
        try {
            val docsnapshot = firestore.collection("users").document(uid).get().await()
            return docsnapshot.toObject<User>()
        }catch (e: Exception){
            throw e
        }
    }
    override suspend fun cekNimUser(nim: String): List<User?>?{
        try {
            val docsnapshot = firestore.collection("users").whereEqualTo("nim",nim).get().await()
            return docsnapshot.toObjects<User>()
        }catch (e: Exception){
            throw e
        }
    }
    override fun getPeoplePraktikum(uids: List<String>): Flow<List<User>> {
        if (uids.isEmpty()) {
            return flowOf(emptyList())
        }
        val uidChunks = uids.chunked(30)
        val flowsPerChunk = uidChunks.map { chunk ->
            fetchUsersChunk(chunk)
        }
        return combine(flowsPerChunk) { arrayOfUserLists ->
            arrayOfUserLists.toList().flatten()
        }
    }
    private fun fetchUsersChunk(uidChunk: List<String>): Flow<List<User>> = callbackFlow {
        val listener = firestore.collection("users")
            .whereIn(FieldPath.documentId(), uidChunk)
            .addSnapshotListener { data, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (data != null) {
                    val list = data.toObjects(User::class.java)
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }
    override fun searchMahasiswa(query: String): Flow<List<User>> = callbackFlow {
        val firestoreQuery = firestore.collection("users") // Ganti dengan nama koleksi Anda
            .orderBy("nim")
            .startAt(query)
            .endAt(query + '\uf8ff')
            .limit(10)
        val listener = firestoreQuery.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val mahasiswaList = snapshot.toObjects(User::class.java)
                trySend(mahasiswaList) // Kirim hasilnya ke Flow
            }
        }
        awaitClose { listener.remove() }
    }

}