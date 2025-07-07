package com.example.reglab7firebase.view.tambahDataPraktikum

import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.Praktikum
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.util.TimeProvider
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TambahPraktikumViewModel(private val praktikumRepo: PraktikumRepo, private val timeProvider: TimeProvider) : ViewModel() {
    private val status = MutableStateFlow<Cek>(Cek.idle)
    val uistatus = status.asStateFlow()
    private val namaDocumentPrak = MutableStateFlow<Praktikum>(Praktikum())
    val uinamDocumentPrak = namaDocumentPrak.asStateFlow()
    fun getJumlahSlot(banyak: Int?) {
        if (banyak != null) {
            namaDocumentPrak.update {
                it.copy(jumlahPertemuan = banyak)
            }
        } else {
            namaDocumentPrak.update {
                it.copy(jumlahPertemuan = 0)
            }
        }
    }

    fun getNamaDocPrak(nama: String) {
        namaDocumentPrak.update {
            it.copy(nama_prak = nama)
        }
    }

    private val tanggalstate = MutableStateFlow<Long?>(null)
    val uitanggal = tanggalstate.asStateFlow()

    fun getTanggal(tanggal: Long?) {
        tanggalstate.value = tanggal
    }

    private val _jamTerpilih = MutableStateFlow<Int?>(null)
    val uiJamTerpilih: StateFlow<Int?> = _jamTerpilih.asStateFlow()

    private val _menitTerpilih = MutableStateFlow<Int?>(null)
    val uiMenitTerpilih: StateFlow<Int?> = _menitTerpilih.asStateFlow()

    fun getJam(jam: Int?) {
        _jamTerpilih.value = jam
    }

    fun getMenit(menit: Int?) {
        _menitTerpilih.value = menit
    }

    val jadwalTimestamp: StateFlow<Timestamp?> = combine(
        tanggalstate,
        _jamTerpilih,
        _menitTerpilih
    ) { tanggalMillis, jam, menit ->
        if (tanggalMillis != null && jam != null && menit != null) {
            val calendar = timeProvider.getCalendarInstance().apply {
                timeInMillis = tanggalMillis
                set(Calendar.HOUR_OF_DAY, jam)
                set(Calendar.MINUTE, menit)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            Timestamp(calendar.time)
        } else {
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun onSubmitHandling() {
        val jadwalTimestamp = jadwalTimestamp.value
        if (jadwalTimestamp == null) {
            status.value = Cek.Error(message = "Silahkan pilih jam & tanggal terlebih dahulu")
        } else if (namaDocumentPrak.value.nama_prak == "") {
            status.value = Cek.Error(message = "Silahkan masukkan nama praktikum")
        } else if (namaDocumentPrak.value.jumlahPertemuan == 0) {
            status.value = Cek.Error(message = "Jumlah Pertemuan tidak boleh kosong")
        } else {
            viewModelScope.launch {
                status.value = Cek.Loading
                try {
                    val prakFinal = uinamDocumentPrak.value.copy(tanggal = jadwalTimestamp)
                    status.value = praktikumRepo.tambahPraktikum(prakFinal)
                } catch (e: Exception) {
                    status.value = Cek.Error(message = e.message.toString())
                }
            }
        }
    }

    fun down() {
        status.value = Cek.idle
    }


}