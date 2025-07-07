package com.example.reglab7firebase.view.praktikumAsprak.detailPraktikumAsprak

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.DetailPertemuan
import com.example.reglab7firebase.data.model.DetailPertemuanLengkap
import com.example.reglab7firebase.data.model.PertemuanPraktikum
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.data.model.TimeRepo
import com.example.reglab7firebase.data.model.UpdatePraktikan
import com.example.reglab7firebase.data.model.User
import com.example.reglab7firebase.data.repository.UserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailAsprakViewModel(
    private val timerepo: TimeRepo,
    private val praktikumRepo: PraktikumRepo,
    private val userrepo: UserRepo,
) : ViewModel() {

    private val pertemuanPrak = MutableStateFlow<List<PertemuanPraktikum>>(emptyList())
    val uipertemuanPrak = pertemuanPrak.asStateFlow()
    private val listMahasiswaCurrent = MutableStateFlow<List<User>>(emptyList())
    val uiMahaiswa = listMahasiswaCurrent.asStateFlow()
    private val status = MutableStateFlow<Cek>(Cek.idle)
    val uistatus = status.asStateFlow()
    private val statusGenrateQrCode = MutableStateFlow<Cek>(Cek.idle)
    val uiStatusGenerateQrCode = statusGenrateQrCode.asStateFlow()
    private val detailLengkap = MutableStateFlow<List<DetailPertemuanLengkap>>(emptyList())
    val uidetailPertemuan = detailLengkap.asStateFlow()
    private val open = MutableStateFlow<Boolean>(false)
    val uiopen = open.asStateFlow()
    private val dataUpdates = MutableStateFlow<UpdatePraktikan>(UpdatePraktikan())
    val uiUpdate = dataUpdates.asStateFlow()
    private val stateUpdateNilai = MutableStateFlow<DetailPertemuan>(DetailPertemuan())
    val uiStateUpdate = stateUpdateNilai.asStateFlow()
    private val stateLogin = MutableStateFlow<User?>(User())
    var uiloginState: StateFlow<User?> = stateLogin.asStateFlow()
    private val statuskehadiran = MutableStateFlow<Boolean>(false)
    val uistatuskehadiran = statuskehadiran.asStateFlow()

    init {
        cekRole()
    }

    fun cekRole() {
        status.value = Cek.Loading
        viewModelScope.launch {
            try {
                val data = userrepo.getUser(userrepo.cekCurent())
                stateLogin.value = data
            } catch (e: Exception) {
                status.value = Cek.idle
            }
        }
    }

    fun changeStatusKehadiran(pilihan: Boolean) {
        statuskehadiran.value = pilihan
    }

    fun openDialog() {
        open.value = true
    }

    fun closDialog() {
        open.value = false
    }

    fun goIdle() {
        status.value = Cek.idle
    }

    fun getPertemuan(idPrak: String) {
        viewModelScope.launch {
            try {
                praktikumRepo.getInfoPertemuanPrak(idPrak = idPrak).collect {
                    pertemuanPrak.value = it
                    status.value = Cek.idle
                }
            } catch (e: Exception) {
                status.value = Cek.Error(message = e.message.toString())
            }
        }
    }

    fun getDetailMahasiswa(detailMap: Map<String, DetailPertemuan>) {
        viewModelScope.launch {
            val uidsToFetch = detailMap.keys.toList()
            userrepo.getPeoplePraktikum(uidsToFetch).catch {
                status.value = Cek.Error(message = it.message.toString())
            }.collect {
                val mahasiswa = it
                val combinedList = detailMap.map { (key, detail) ->
                    val mahasiswa = mahasiswa.find { it.uid == key }
                    DetailPertemuanLengkap(
                        nim = mahasiswa?.nim,
                        detailPertemuan = detail
                    )
                }
                detailLengkap.value = combinedList
            }
        }
    }

    fun updateNilai(nilaibaru: String) {
        if (nilaibaru.isNotEmpty()) stateUpdateNilai.update {
            it.copy(nilai = nilaibaru.toInt())
        } else stateUpdateNilai.update {
            it.copy(nilai = 0)
        }
    }

    fun onUpdateData(
        idPrak: String,
        idPertemuan: String,
        idMahasiswa: String,
        data: DetailPertemuan,
    ) {
        dataUpdates.update {
            it.copy(
                data = data,
                idPrak = idPrak,
                idPertemuan = idPertemuan,
                idMahasiswa = idMahasiswa
            )
        }
    }

    fun subbitHandling() {
        status.value = Cek.Loading
        if (uiUpdate.value.idMahasiswa.isEmpty() || uiUpdate.value.idPrak.isEmpty() || uiUpdate.value.idPertemuan.isEmpty()) {
            status.value = Cek.Error(message = "Gagal mahasiswa tidak ditemukan")
        } else if (stateUpdateNilai.value.nilai == 0)
            status.value = Cek.Error("Field Tidak Boleh Kosong")
        else {
            stateUpdateNilai.update {
                it.copy(
                    uid_mahasiswa = uiUpdate.value.data.uid_mahasiswa,
                    status_kehadiran = statuskehadiran.value
                )
            }
            viewModelScope.launch {
                try {
                    praktikumRepo.updatePraktikan(
                        idPrak = uiUpdate.value.idPrak,
                        idPertemuan = uiUpdate.value.idPertemuan,
                        data = uiStateUpdate.value,
                        idMahasiswa = uiUpdate.value.idMahasiswa
                    )
                    status.value = Cek.Sukses
                } catch (e: Exception) {
                    status.value = Cek.Error(e.message.toString())
                }

            }
        }
    }

}