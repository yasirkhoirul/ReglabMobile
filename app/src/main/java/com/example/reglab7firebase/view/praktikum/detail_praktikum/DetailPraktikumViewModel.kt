package com.example.reglab7firebase.view.praktikum.detail_praktikum

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.DetailPertemuan
import com.example.reglab7firebase.data.model.PertemuanPraktikum
import com.example.reglab7firebase.data.model.PraktikumRepo
import com.example.reglab7firebase.data.repository.UserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailPraktikumViewModel(
    private val pertemuanprak: PraktikumRepo,
    private val cekuser: UserRepo
): ViewModel() {

    private val pertemuanPraktikumstate = MutableStateFlow<List<PertemuanPraktikum>>(emptyList())
    val uiPertemuanPraktikumState = pertemuanPraktikumstate.asStateFlow()
    private val status = MutableStateFlow<Cek>(Cek.idle)
    val uiStatus = status.asStateFlow()
    private val uidUser = MutableStateFlow<String>("")
    val uiUiduser = uidUser.asStateFlow()
    init {
        uidUser.value = cekuser.cekCurent()
    }

    val uidetailpertemuan: StateFlow<Map<String, DetailPertemuan?>> = uiPertemuanPraktikumState
        .map {
        it.associate {
            val nilai = it.detail_pertemuan[cekuser.cekCurent()]
            (it.id_pertemuan) to nilai
        }.filter {
            it.key.isNotBlank()
        }
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyMap() )



    fun getPertemuanUser(idPrak: String,){
        status.value = Cek.Loading
        if (cekuser.cekUser()){
            val uid = cekuser.cekCurent()
            viewModelScope.launch {
                try {
                    val listpertemuan = pertemuanprak.getAllPertemuanUserCall(idPrak,uid)
                    listpertemuan
                        .catch {
                            throw it
                        }
                        .collect {
                        pertemuanPraktikumstate.value = it
                        Log.d("listpertemuan",it.toString())
                    }
                }catch (e: Exception){
                    Log.d("listpertemuan",e.message.toString())
                    throw e
                }

            }
        }else{
            status.value = Cek.Error(message = "belum login")
        }
    }
}