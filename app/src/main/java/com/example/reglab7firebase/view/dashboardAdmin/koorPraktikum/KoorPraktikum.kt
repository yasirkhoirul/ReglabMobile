package com.example.reglab7firebase.view.dashboardAdmin.koorPraktikum

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.data.repository.PraktikumRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.view.component.ContentKoor
import com.example.reglab7firebase.view.component.Loading

@Composable
fun KoorPraktikum(
    modifier: Modifier = Modifier,
    viewModel: KoorPraktikumViewModel = viewModel(
        factory = AppViewModelFactory(
            userRepo = ImplementasiUserRepo(),
            praktikumRepo = PraktikumRepoImpl()
        )
    ),
) {

    val listprak by viewModel.uilistPraktikum.collectAsStateWithLifecycle()
    val status by viewModel.uistatus.collectAsStateWithLifecycle()
    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Log.d("hasilnya", status.toString())
            when (status) {
                is Cek.Error -> {
                    Text((status as Cek.Error).message)
                }

                Cek.Loading -> {
                    Loading()
                }

                Cek.Sukses -> ContentKoor(
                    praktikum = listprak,
                    clickDetail = { },
                    cllickHandling = { idMahasiswa, idprak ->
                        viewModel.clickHandling(
                            idPrak = idprak,
                            idMhs = idMahasiswa
                        )
                    }, viewmodel = viewModel
                )

                Cek.idle -> {
                    Loading()
                }
            }

        }
    }

}