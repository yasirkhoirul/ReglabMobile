package com.example.reglab7firebase.view.praktikumAdmin.detailPraktikumAdmin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.Praktikum
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.data.model.User
import com.example.reglab7firebase.data.repository.UserRepo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class DetailPraktikumAdminViewModel(
    private val userRepo: UserRepo,
    private val prakrepo: PraktikumRepo,
) : ViewModel() {
    private var getAsprakJob: Job? = null
    private val praktikumDetail = MutableStateFlow<Praktikum?>(Praktikum())
    val uiPraktikumDetail = praktikumDetail.asStateFlow()
    private val status = MutableStateFlow<Cek>(Cek.idle)
    val uistatus = status.asStateFlow()
    private val listAsprakCurrentPraktikumstate = MutableStateFlow<List<User>>(emptyList())
    val uilistAsprakCurrent = listAsprakCurrentPraktikumstate.asStateFlow()
    private val listMahasiswaCurrent = MutableStateFlow<List<User>>(emptyList())
    val uiListMahasiswaCurrent = listMahasiswaCurrent.asStateFlow()

    init {
        observePraktikumChanges()
    }

    private fun observePraktikumChanges() {
        viewModelScope.launch {
            praktikumDetail.collect { praktikumData ->
                if (praktikumData != null) {
                    getDetailAsprak(uiPraktikumDetail.value?.uid_asprak ?: emptyList())
                    getDetailMahasiswa(uiPraktikumDetail.value?.uid_mahasiswa ?: emptyList())
                } else {
                    listMahasiswaCurrent.value = emptyList()
                    listAsprakCurrentPraktikumstate.value = emptyList()
                }
            }
        }
    }

    fun getUser(dokumenid: String) {
        status.value = Cek.Loading
        if (dokumenid.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    prakrepo.getPrakCall(dokumenid).collect {
                        praktikumDetail.value = it
                        status.value = Cek.idle
                    }
                } catch (e: Exception) {
                    status.value = Cek.Error(message = e.message.toString())
                }
            }
        } else {
            status.value = Cek.Error(message = "Terjadi Kesalahan Silahkan Coba Lagi")
        }
    }

    fun getDetailAsprak(uidListAsprak: List<String>) {
        getAsprakJob?.cancel()
        if (uidListAsprak.isEmpty()) {
            listAsprakCurrentPraktikumstate.value = emptyList()
            return
        }
        getAsprakJob = viewModelScope.launch {
            try {
                userRepo.getPeoplePraktikum(uidListAsprak).catch {
                    // Pastikan coroutine masih aktif sebelum update state
                    if (isActive) {
                        status.value = Cek.Error(it.message.toString())
                    }
                }.collect {
                    listAsprakCurrentPraktikumstate.value =
                        if (uidListAsprak.isEmpty()) emptyList() else it
                }
            } catch (e: Exception) {
                status.value = Cek.Error(e.message.toString())
                throw e
            }
        }

    }

    fun getDetailMahasiswa(uidListAsprak: List<String>) {
        if (uidListAsprak.isEmpty()) {
            listMahasiswaCurrent.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                userRepo.getPeoplePraktikum(uidListAsprak).catch {
                    status.value = Cek.Error(message = it.message.toString())
                }.collect {
                    listMahasiswaCurrent.value = it
                }
            } catch (e: Exception) {
                status.value = Cek.Error(e.message.toString())
                throw e
            }
        }
    }

    fun getDeleteMahasiswa(uid: String, idPrak: String) {
        if (uid.isNotEmpty() && idPrak.isNotEmpty()) {
            status.value = Cek.Loading
            viewModelScope.launch {
                val result = prakrepo.hapusOrang(idPrak = idPrak, uidMahasiswaUntukDihapus = uid)
                if (!result) {
                    status.value = Cek.Error("Gagal menghapus")
                }
            }
        } else {
            status.value = Cek.Error("Gagal Memilih")
        }
    }

    fun getDeleteAsprak(uid: String, idPrak: String) {
        status.value = Cek.Loading
        if (uid.isEmpty() && idPrak.isEmpty()) {
            status.value = Cek.Error(message = "uid dan id praktikum kosong")
        } else {
            viewModelScope.launch {
                status.value = Cek.Loading
                try {
                    val hasil =
                        prakrepo.hapusAsprak(idPrak = idPrak, uidMahasiswaUntukDihapus = uid)
                    if (hasil) {
                    } else {
                        status.value = Cek.Error("Gagal")
                    }
                } catch (e: Exception) {
                    status.value = Cek.Error(message = e.message.toString())
                }
            }

        }
    }

    fun GoIdle() {
        status.value = Cek.idle
    }
}