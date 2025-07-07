package com.example.reglab7firebase.view.dashboardAdmin.koorPraktikum

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.Praktikum
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.data.model.User
import com.example.reglab7firebase.data.repository.UserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class KoorPraktikumViewModel(
    val praktikum: PraktikumRepo,
    private val user: UserRepo,
) : ViewModel() {

    private val listPraktikum = MutableStateFlow<List<Praktikum>>(emptyList())
    val uilistPraktikum = listPraktikum.asStateFlow()
    private val status = MutableStateFlow<Cek>(Cek.Loading)
    val uistatus = status.asStateFlow()
    private val UsernIm = MutableStateFlow<List<User>>(emptyList())
    val uiUsernIm = UsernIm.asStateFlow()
    private val statusnim = MutableStateFlow<Cek>(Cek.Loading)
    val uistatusnim = statusnim.asStateFlow()

    init {
        getAllprak()
    }

    fun getAllprak() {
        viewModelScope.launch {
            try {
                praktikum.getAllPrakAdminCall().collect {
                    listPraktikum.value = it
                    status.value = Cek.Sukses
                }
            } catch (e: Exception) {
                status.value = Cek.Error(message = "Gagal memuat praktikum error: ${e.message}")
            }
        }
    }

    fun clickHandling(idPrak: String, idMhs: String) {
        status.value = Cek.Loading
        viewModelScope.launch {
            try {
                praktikum.addRole(idPrak = idPrak, idMaahasiswa = idMhs)
                status.value = Cek.Sukses
            } catch (e: Exception) {
                status.value = Cek.Error(message = "Gagal menambah koor")
            }
        }
    }

    fun getDetailAsprak(uidListAsprak: List<String>) {
        statusnim.value = Cek.Loading
        viewModelScope.launch {
            try {
                user.getPeoplePraktikum(uidListAsprak).catch {
                    // Pastikan coroutine masih aktif sebelum update state
                    if (isActive) {
                        status.value = Cek.Error(it.message.toString())
                    }
                }.collect {
                    UsernIm.value = if (uidListAsprak.isEmpty()) emptyList() else it
                    statusnim.value = Cek.Sukses
                }
            } catch (e: Exception) {
                status.value = Cek.Error(e.message.toString())
                Log.d("hasilasprak", e.message.toString())
                throw e
            }
        }

    }

}