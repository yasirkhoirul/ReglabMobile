package com.example.reglab7firebase.data.repository

import android.icu.util.Calendar
import android.util.Log
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.DetailPertemuan
import com.example.reglab7firebase.data.model.PertemuanPraktikum
import com.example.reglab7firebase.data.model.PertemuanPraktikumkhusus
import com.example.reglab7firebase.data.model.Praktikum
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PraktikumRepoImpl : PraktikumRepo {
    private val firestore = Firebase.firestore

    override suspend fun getAllPrakForUser(uid: String): List<Praktikum> {
        try {
            val docsnapshot =
                firestore.collection("praktikum").whereArrayContains("uid_mahasiswa", uid).get()
                    .await()
            return docsnapshot.toObjects<Praktikum>()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllPrakForAsprak(uid: String): List<Praktikum> {
        try {
            val docsnapshot =
                firestore.collection("praktikum").whereArrayContains("uid_asprak", uid).get()
                    .await()
            return docsnapshot.toObjects<Praktikum>()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getOnePrak(uid: String): Praktikum? {
        try {
            val hasil = firestore.collection("praktikum").document(uid).get().await()
            return hasil.toObject<Praktikum?>()
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getPrakCall(uid: String): Flow<Praktikum?> = callbackFlow {
        val hasil =
            firestore.collection("praktikum").document(uid).addSnapshotListener { data, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (data != null) {
                    val data = data.toObject<Praktikum>()
                    trySend(data)
                }
            }
        awaitClose {
            hasil.remove()
        }
    }

    override suspend fun deletePrak(uidDocument: String) {
        try {
            firestore.collection("praktikum").document(uidDocument).delete().await()
        } catch (e: Exception) {
            throw e
        }
    }

    override fun getAllPrakAdminCall(): Flow<List<Praktikum>> =
        callbackFlow {
            val listener = firestore.collection("praktikum").addSnapshotListener { data, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (data != null) {
                    val list = data.documents.mapNotNull {
                        it.toObject(Praktikum::class.java)
                    }
                    trySend(list)
                }
            }

            awaitClose {
                listener.remove()
            }
        }

    override fun getAllPertemuanUserCall(idPrak: String, uid: String): Flow<List<PertemuanPraktikum>> =
        callbackFlow {
            val listener =
                firestore.collection("praktikum").document(idPrak).collection("pertemuan_praktikum")
                    .whereNotEqualTo("detail_pertemuan.$uid", null)
                    .addSnapshotListener { data, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }
                        if (data != null) {
                            val list = data.documents.mapNotNull {
                                it.toObject(PertemuanPraktikum::class.java)
                            }
                            trySend(list)
                        }
                    }
            awaitClose {
                listener.remove()
            }
        }

    override fun getInfoPertemuanPrak(idPrak: String): Flow<List<PertemuanPraktikum>> =
        callbackFlow {
            val listener =
                firestore.collection("praktikum").document(idPrak).collection("pertemuan_praktikum")
                    .addSnapshotListener { data, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }
                        if (data != null) {
                            val list = data.documents.mapNotNull {
                                it.toObject(PertemuanPraktikum::class.java)
                            }
                            trySend(list)
                        }
                    }
            awaitClose {
                listener.remove()
            }
        }

    override suspend fun cekJumlahMasuk(idMahasiswa: String, idPrak: String): List<PertemuanPraktikum> {
        val data =
            firestore.collection("praktikum").document(idPrak).collection("pertemuan_praktikum")
                .whereEqualTo("detail_pertemuan.$idMahasiswa.status_kehadiran", true).get().await()
        return data.toObjects<PertemuanPraktikum>()
    }

    override fun getAllPertemuanUserCallRentang(
        idPrak: String,
        waktuSekarang: Timestamp,
        waktuSatuMingguLagi: Timestamp,
        nama: String,
        jumlahmasuk: Int,
        jumlahpertemuan: Int,
    ): Flow<List<PertemuanPraktikumkhusus>> =
        callbackFlow {
            val listener =
                firestore.collection("praktikum").document(idPrak).collection("pertemuan_praktikum")
                    .whereGreaterThanOrEqualTo("tanggal", waktuSekarang)
                    .whereLessThanOrEqualTo("tanggal", waktuSatuMingguLagi)
                    .addSnapshotListener { data, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }
                        if (data != null) {
                            val list = data.documents.mapNotNull {
                                val data = it.toObject(PertemuanPraktikumkhusus::class.java)
                                data?.copy(
                                    id_prak = idPrak,
                                    namaPrak = nama,
                                    jumlahmask = jumlahmasuk,
                                    jumlahpertemuan = jumlahpertemuan
                                )
                            }
                            Log.d("hasil data praktikum terdekat", list.toString())
                            trySend(list)
                        }
                    }
            awaitClose {
                listener.remove()
            }
        }

    override suspend fun tambahMahasiswaPraktikum(uidMahasiswa: List<String>, idPrak: String): Boolean {
        val batch = firestore.batch()
        if (uidMahasiswa.isEmpty()) {
            Log.d("Firestore", "Tidak ada mahasiswa baru untuk ditambahkan.")
            return false
        } else {
            val ref = firestore.collection("praktikum").document(idPrak)
            try {
                val semuaPertemuanSnapshot = ref.collection("pertemuan_praktikum").get().await()
                val daftarDokumenPertemuan = semuaPertemuanSnapshot.documents


                batch.update(
                    ref,
                    "uid_mahasiswa",
                    FieldValue.arrayUnion(*uidMahasiswa.toTypedArray())
                )//updatepertama
//                ref.update("uid_mahasiswa", FieldValue.arrayUnion(*uidMahasiswa.toTypedArray())).await()

                for (i in uidMahasiswa) {
                    for (pertemuanDoc in daftarDokumenPertemuan) {
                        // `pertemuanDoc.reference` adalah DocumentReference untuk setiap dokumen pertemuan
                        val pertemuanRef = pertemuanDoc.reference

                        // Kunci dari pembaruan map adalah menggunakan "Dot Notation"
                        // Ini akan membuat field baru di dalam map `detail_pertemuan`
                        // dengan key adalah UID mahasiswa.
                        val fieldPath = "detail_pertemuan.$i"

                        // Tambahkan perintah update untuk dokumen pertemuan ini ke batch
                        batch.update(
                            pertemuanRef,
                            fieldPath,
                            DetailPertemuan(nilai = 0, status_kehadiran = false, uid_mahasiswa = i)
                        )
                    }
                }
                batch.commit().await()
                return true
            } catch (e: Exception) {
                return false
            }
        }
    }

    override suspend fun tambahAsprakPraktikum(uidMahasiswa: List<String>, idPrak: String): Boolean {
        val batch = firestore.batch()
        if (uidMahasiswa.isEmpty()) {
            Log.d("Firestore", "Tidak ada mahasiswa baru untuk ditambahkan.")
            return false
        } else {
            try {
                val ref = firestore.collection("praktikum").document(idPrak)

                batch.update(ref, "uid_asprak", FieldValue.arrayUnion(*uidMahasiswa.toTypedArray()))
                for (i in uidMahasiswa) {
                    val userRepo = firestore.collection("users").document(i)
                    batch.update(userRepo, "role", "asprak")
                }
                batch.commit().await()
                return true
            } catch (e: Exception) {
                return false
            }
        }
    }

    override suspend fun hapusOrang(idPrak: String, uidMahasiswaUntukDihapus: String): Boolean {
        if (uidMahasiswaUntukDihapus.isBlank()) {
            Log.w("Firestore", "UID mahasiswa yang akan dihapus tidak boleh kosong.")
            return false
        }
        val praktikumRef = firestore.collection("praktikum").document(idPrak)

        return try {
            // Langkah 1: Ambil semua dokumen pertemuan yang ada untuk di-update
            val semuaPertemuanSnapshot =
                praktikumRef.collection("pertemuan_praktikum").get().await()
            val daftarDokumenPertemuan = semuaPertemuanSnapshot.documents

            // Langkah 2: Siapkan WriteBatch
            val batch = firestore.batch()

            // Langkah 3: Tambahkan operasi untuk DOKUMEN INDUK ke batch
            // Gunakan FieldValue.arrayRemove untuk menghapus UID dari list
            batch.update(
                praktikumRef,
                "uid_mahasiswa",
                FieldValue.arrayRemove(uidMahasiswaUntukDihapus)
            )

            // Langkah 4: Tambahkan operasi untuk setiap dokumen di SUB-KOLEKSI

            // Loop untuk setiap dokumen pertemuan yang ada
            for (pertemuanDoc in daftarDokumenPertemuan) {
                val pertemuanRef = pertemuanDoc.reference

                // Siapkan path ke key map yang akan dihapus menggunakan Dot Notation
                val fieldPathUntukDihapus = "detail_pertemuan.$uidMahasiswaUntukDihapus"

                // Tambahkan perintah update dengan FieldValue.delete()
                // Ini akan menghapus key "user_cici_789" dari map `detail_pertemuan`
                batch.update(pertemuanRef, fieldPathUntukDihapus, FieldValue.delete())
            }

            // Langkah 5: Jalankan semua operasi penghapusan sekaligus
            batch.commit().await()

            Log.d(
                "Firestore",
                "Sukses! Mahasiswa $uidMahasiswaUntukDihapus telah dihapus dari sistem praktikum $idPrak."
            )
            true

        } catch (e: Exception) {
            Log.e("Firestore", "Operasi batch untuk menghapus mahasiswa gagal.", e)
            false
        }
    }

    override suspend fun hapusAsprak(idPrak: String, uidMahasiswaUntukDihapus: String): Boolean {
        if (idPrak.isEmpty() && uidMahasiswaUntukDihapus.isEmpty()) {
            return false
        } else {
            try {
                val ref = firestore.collection("praktikum").document(idPrak)
                ref.update("uid_asprak", FieldValue.arrayRemove(uidMahasiswaUntukDihapus)).await()
                val data = getAllPrakForAsprak(uidMahasiswaUntukDihapus)
                if (data.isEmpty()) {
                    val userRef = firestore.collection("users")
                    userRef.document(uidMahasiswaUntukDihapus).update("role", "mahasiswa").await()
                }
                return true
            } catch (e: Exception) {
                throw e
                return false
            }
        }
    }

    override suspend fun updatePraktikan(
        idPrak: String,
        idPertemuan: String,
        idMahasiswa: String,
        data: DetailPertemuan,
    ) {
        Log.d("FirestoreDebug", "Mencoba update dengan path:")
        Log.d("FirestoreDebug", "  -> idPrak: $idPrak")
        Log.d("FirestoreDebug", "  -> idPertemuan: $idPertemuan")
        Log.d("FirestoreDebug", "  -> idMahasiswa: $idMahasiswa")
        Log.d("FirestoreDebug", "  -> Nilai Baru: ${data.nilai}")

        try {
            val nilai = "detail_pertemuan.$idMahasiswa.nilai"
            val kehadiran = "detail_pertemuan.$idMahasiswa.status_kehadiran"
            firestore.collection("praktikum").document(idPrak).collection("pertemuan_praktikum")
                .document(idPertemuan).update(nilai, data.nilai,kehadiran,data.status_kehadiran).addOnSuccessListener {
                    // Tambahkan log sukses untuk debugging
                    Log.d("FirestoreUpdate", "Sukses mengupdate nilai untuk $idMahasiswa")
                }
                .addOnFailureListener { e ->
                    // Tambahkan log error untuk debugging
                    Log.e("FirestoreUpdate", "Gagal mengupdate nilai", e)
                }.await()
        } catch (e: Exception) {
            Log.e("FirestoreUpdate", "Terjadi exception saat mencoba update", e)
            throw e
        }

    }

    override fun getOnePertemuanPrak(idPrak: String, idPertemuan: String): Flow<PertemuanPraktikum?> =
        callbackFlow {
            Log.d("waktu","id pertemuan $idPertemuan")
            val data =
                firestore.collection("praktikum").document(idPrak).collection("pertemuan_praktikum")
                    .document(idPertemuan).addSnapshotListener { data, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }
                        if (data != null) {
                            val data = data.toObject<PertemuanPraktikum>()
                            trySend(data)
                        }
                    }
            awaitClose {
                data.remove()
            }
        }

    override suspend fun cekMahasiswa(idMahsiswa: String): List<Praktikum>{
        try {
            val cek = firestore.collection("praktikum").whereArrayContains("uid_mahasiswa",idMahsiswa).get().await()
            return cek.toObjects<Praktikum>()
        }catch (e: Exception){
            throw e
        }

    }


    override suspend fun addPresensiUser(idPrak: String, idPertemuan: String, idMahasiswa: String) {
        try {
        firestore.collection("praktikum").document(idPrak).collection("pertemuan_praktikum")
            .document(idPertemuan).update("detail_pertemuan.$idMahasiswa.status_kehadiran", true).await()
        }catch (e: Exception){
            throw e
        }
    }

    override suspend fun addRole(idPrak: String, idMaahasiswa: String){
        try {
            firestore.collection("praktikum").document(idPrak).update("koor",idMaahasiswa).await()
        }catch (e: Exception){
            throw e
        }
    }
    override suspend fun tambahPraktikum(praktikum: Praktikum): Cek {
        return try {
            val batch = firestore.batch()
            val calendar = Calendar.getInstance()

            // 1. Buat referensi dokumen baru untuk praktikum utama
            val docRef = firestore.collection("praktikum").document()

            // 2. Siapkan data praktikum final dengan ID yang baru dibuat
            val finalPraktikum = praktikum.copy(id_prak = docRef.id)
            batch.set(docRef, finalPraktikum)

            // 3. Atur tanggal mulai dari data praktikum
            calendar.time = finalPraktikum.tanggal?.toDate()

            // 4. Loop untuk membuat dokumen pertemuan di sub-koleksi
            for (i in 1..finalPraktikum.jumlahPertemuan) {
                if (i > 1) {
                    // Tambah 7 hari untuk pertemuan berikutnya
                    calendar.add(Calendar.DAY_OF_YEAR, 7)
                }
                val idPertemuan = "pertemuan_%02d".format(i)
                val pertemuanRef = docRef.collection("pertemuan_praktikum").document(idPertemuan)

                val pertemuanData = PertemuanPraktikum(
                    id_pertemuan = idPertemuan,
                    nama_pertemuan = "Pertemuan ke-$i",
                    tanggal = Timestamp(calendar.time)
                )
                batch.set(pertemuanRef, pertemuanData)
            }

            // 5. Jalankan semua operasi tulis sekaligus
            batch.commit().await()
            Cek.Sukses

        } catch (e: Exception) {
            // Jika terjadi error, kembalikan status Error
            Cek.Error(e.message ?: "Gagal menambahkan praktikum")
        }
    }

}