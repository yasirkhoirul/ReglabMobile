package com.example.reglab7firebase.view.praktikum.praktikumTerdekat

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.Khusus
import com.example.reglab7firebase.data.model.PertemuanPraktikumkhusus
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.data.repository.UserRepo
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PraktikumTerdekatViewModel(
    private val prakum: PraktikumRepo,
    private val user: UserRepo,
) : ViewModel() {

    private val jadwalTerdekat = MutableStateFlow<List<PertemuanPraktikumkhusus>>(emptyList())
    val uiJadwal: StateFlow<List<PertemuanPraktikumkhusus>> = jadwalTerdekat.asStateFlow()
    private val status = MutableStateFlow<Cek>(Cek.idle)
    val uistatus = status.asStateFlow()

    init {
        Log.d("ViewModel", "Jadwal mingguan dibuat")
        getPrak()
    }

    fun down() {
        status.value = Cek.idle
    }

    fun getPrak() {
        status.value = Cek.Loading
        Log.d("ViewModel", "masukprak")
        val waktuSekarang = Timestamp.now()
        val calendar = Calendar.getInstance()
        calendar.time = waktuSekarang.toDate() // Mulai dari waktu sekarang
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val waktuSatuMingguLagi = Timestamp(calendar.time)
        viewModelScope.launch {
            try {
                Log.d("status prkterdetkat", "masuk try")
                val data = prakum.getAllPrakForUser(user.cekCurent())
                if (data.isEmpty()) {
                    jadwalTerdekat.value = emptyList()
                    status.value = Cek.Sukses
                    return@launch
                }
                val daftarIdPrak: List<Khusus> = data.map {
                    Khusus(
                        id_prak = it.id_prak,
                        nama = it.nama_prak,
                        jumlahpertemuan = it.jumlahPertemuan
                    )
                }
                val flowsPertemuan = daftarIdPrak.map {
                    val datalist =
                        prakum.cekJumlahMasuk(idMahasiswa = user.cekCurent(), idPrak = it.id_prak)
                    prakum.getAllPertemuanUserCallRentang(
                        idPrak = it.id_prak,
                        waktuSekarang = waktuSekarang,
                        waktuSatuMingguLagi = waktuSatuMingguLagi,
                        nama = it.nama,
                        jumlahmasuk = datalist.size,
                        jumlahpertemuan = it.jumlahpertemuan
                    )
                }
                if (flowsPertemuan.isNotEmpty()) {
                    combine(flowsPertemuan) { arrayOfLists ->
                        val listGabungan = arrayOfLists.toList().flatten()
                        listGabungan.sortedBy { it.tanggal }
                    }.collect { listFinal ->
                        Log.d("status prkterdetkat", "masuk coolect")
                        status.value = Cek.Sukses
                        jadwalTerdekat.value = listFinal
                        Log.d("ViewModel", "Jadwal mingguan diupdate: ${listFinal.size} pertemuan.")
                    }
                }
                status.value = Cek.Sukses
            } catch (e: Exception) {
                status.value = Cek.Error(message = e.message.toString())
            }
        }
    }
}