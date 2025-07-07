package com.example.reglab7firebase.data.model

import kotlinx.coroutines.flow.Flow

data class Isi(
    val uid: String = "",
    val judul: String = "",
    val isi: String = ""
)

interface InfoRepo {
    fun getInformation(): Flow<List<Isi>>
    suspend fun getDetailInformation(uid: String): Isi?
    suspend fun addInformation(datamasuk: Isi)
    suspend fun deleteInformation(id: String)
}