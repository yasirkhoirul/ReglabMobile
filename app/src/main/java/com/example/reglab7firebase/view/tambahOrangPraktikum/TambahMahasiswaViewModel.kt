package com.example.reglab7firebase.view.tambahOrangPraktikum

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.data.model.User
import com.example.reglab7firebase.data.repository.UserRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class TambahMahasiswaViewModel(
    private val praktikum: PraktikumRepo,
    private val user: UserRepo,
) : ViewModel() {

    private val searhstate = MutableStateFlow("")
    val uisearch: StateFlow<String> = searhstate.asStateFlow()
    private val hasildipilih = MutableStateFlow<List<String>>(emptyList())
    val uihasildipilih = hasildipilih.asStateFlow()
    private val status = MutableStateFlow<Cek>(Cek.idle)
    val uistatus = status.asStateFlow()

    fun down() {
        status.value = Cek.idle
    }

    fun tambahmahasiswadipilih(mahasiswa: String) {
        if (!hasildipilih.value.contains(mahasiswa)) {
            hasildipilih.value = hasildipilih.value + mahasiswa
        }
    }

    fun hapusmahasiswadipilih(mahasiswa: String) {
        hasildipilih.value = hasildipilih.value - mahasiswa
    }


    fun onSearchQueryChanged(query: String) {
        searhstate.value = query
    }

    init {
        CariMhasiswa()
    }

    private val hasilsearchstate = MutableStateFlow<List<User>>(emptyList())
    val uiHasilPencarian: StateFlow<List<User>> = hasilsearchstate.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    fun CariMhasiswa() {
        viewModelScope.launch {
            try {
                uisearch.debounce(300).flatMapLatest {
                    if (it.isBlank()) {
                        flowOf(emptyList())
                    } else {
                        user.searchMahasiswa(it)
                    }
                }.collect {
                    hasilsearchstate.value = it
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun cekMahasiswatoAsprak(uidYangAKandimasukkan: List<String>, cekData: List<String>): Boolean {
        if (uidYangAKandimasukkan.isEmpty()) {
            return false
        } else {
            try {
                val duplikasi = uidYangAKandimasukkan.intersect(cekData.toSet())
                return duplikasi.isEmpty()
            } catch (e: Exception) {
                Log.e("Validation", "Error saat melakukan validasi", e)
                return false
            }
        }
    }

    fun tambahMahasiswa(idpraks: String, lists: List<String>) {
        status.value = Cek.Loading
        if (lists.isEmpty()){
            status.value = Cek.Error("Belum ada mahasiswa yang dipilih")
            return
        }
        viewModelScope.launch {
            try {
                val hasilDataPRak = praktikum.getOnePrak(idpraks)
                if (!cekMahasiswatoAsprak(lists, hasilDataPRak?.uid_asprak ?: emptyList())) {
                    status.value = Cek.Error(message = "Uid sudah terdaftar sebgai asprak")
                    return@launch
                }
                val hasil =
                    praktikum.tambahMahasiswaPraktikum(idPrak = idpraks, uidMahasiswa = lists)
                if (hasil) status.value = Cek.Sukses else status.value =
                    Cek.Error(message = "Gagal tambah ke praktikum")
            } catch (e: Exception) {
                status.value = Cek.Error(message = e.message.toString())
            }
        }
    }

    fun tambahAsprak(idpraks: String, lists: List<String>) {
        status.value = Cek.Loading
        if (lists.isEmpty()){
            status.value = Cek.Error("Belum ada asprakyang dipilih")
            return
        }
        viewModelScope.launch {
            try {
                val hasilDataPRak = praktikum.getOnePrak(idpraks)
                if (!cekMahasiswatoAsprak(lists, hasilDataPRak?.uid_mahasiswa ?: emptyList())) {
                    status.value = Cek.Error(message = "Uid sudah terdaftar sebgai mahasiswa")
                    return@launch
                }
                val hasil = praktikum.tambahAsprakPraktikum(idPrak = idpraks, uidMahasiswa = lists)
                if (hasil) status.value = Cek.Sukses else status.value =
                    Cek.Error(message = "Gagal tambah ke praktikum")
            } catch (e: Exception) {
                status.value = Cek.Error(message = e.message.toString())
            }
        }

    }

}