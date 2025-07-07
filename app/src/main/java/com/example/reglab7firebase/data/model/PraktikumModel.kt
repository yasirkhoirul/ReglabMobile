package com.example.reglab7firebase.data.model

import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow

data class Praktikum(
    val id_prak: String = "",
    val nama_prak: String ="",
    val slot: String = "",
    val uid_asprak: List<String> = emptyList<String>(),
    val uid_mahasiswa: List<String> = emptyList<String>(),
    val tanggal: Timestamp? = null,
    val jumlahPertemuan: Int = 0,
    val koor: String = ""
    )

data class PertemuanPraktikum(
    val detail_pertemuan: Map<String, DetailPertemuan> =emptyMap<String, DetailPertemuan>(),
    val id_pertemuan: String = "",
    val nama_pertemuan: String = "",
    val tanggal: Timestamp = Timestamp.now()
    )

data class DetailPertemuan(
    val nilai : Int = 0,
    val status_kehadiran: Boolean = false,
    val uid_mahasiswa: String = ""
)
data class Khusus(val id_prak: String = "",val nama: String = "",val jumlahpertemuan: Int =0)
data class PertemuanPraktikumkhusus(
    val namaPrak : String = "",
    val id_prak: String = "",
    val detail_pertemuan: Map<String, DetailPertemuan> =emptyMap<String, DetailPertemuan>(),
    val id_pertemuan: String = "",
    val nama_pertemuan: String = "",
    val tanggal: Timestamp = Timestamp.now(),
    val jumlahmask: Int = 0,
    val jumlahpertemuan: Int =0,
    val koor: String = ""
)


data class DetailPertemuanLengkap(
    val nim: String? = "",
    val detailPertemuan: DetailPertemuan = DetailPertemuan()
)


interface PraktikumRepo {
    suspend fun getAllPrakForUser(uid: String): List<Praktikum>
    suspend fun getAllPrakForAsprak(uid: String): List<Praktikum>
    suspend fun getOnePrak(uid: String): Praktikum?
    fun getPrakCall(uid: String): Flow<Praktikum?>
    suspend fun deletePrak(uidDocument: String)
    fun getAllPrakAdminCall(): Flow<List<Praktikum>>
    fun getAllPertemuanUserCall(idPrak: String, uid: String): Flow<List<PertemuanPraktikum>>
    fun getInfoPertemuanPrak(idPrak: String): Flow<List<PertemuanPraktikum>>
    suspend fun cekJumlahMasuk(idMahasiswa: String, idPrak: String): List<PertemuanPraktikum>
    fun getAllPertemuanUserCallRentang(
        idPrak: String,
        waktuSekarang: Timestamp,
        waktuSatuMingguLagi: Timestamp,
        nama: String,
        jumlahmasuk: Int,
        jumlahpertemuan: Int
    ): Flow<List<PertemuanPraktikumkhusus>>
    suspend fun tambahMahasiswaPraktikum(uidMahasiswa: List<String>, idPrak: String): Boolean
    suspend fun tambahAsprakPraktikum(uidMahasiswa: List<String>, idPrak: String): Boolean
    suspend fun hapusOrang(idPrak: String, uidMahasiswaUntukDihapus: String): Boolean
    suspend fun hapusAsprak(idPrak: String, uidMahasiswaUntukDihapus: String): Boolean
    suspend fun updatePraktikan(
        idPrak: String,
        idPertemuan: String,
        idMahasiswa: String,
        data: DetailPertemuan
    )
    fun getOnePertemuanPrak(idPrak: String, idPertemuan: String): Flow<PertemuanPraktikum?>
    suspend fun cekMahasiswa(idMahsiswa: String): List<Praktikum>
    suspend fun addPresensiUser(idPrak: String, idPertemuan: String, idMahasiswa: String)
    suspend fun addRole(idPrak: String, idMaahasiswa: String)
    suspend fun tambahPraktikum(praktikum: Praktikum): Cek
}

interface TimeRepo {
    suspend fun fetchServerTimeViaDummyDoc(uiduser: String): Timestamp
}
