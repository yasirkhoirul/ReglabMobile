package com.example.reglab7firebase.view.praktikumAsprak

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.data.repository.PraktikumRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.view.component.ContentContainer
import com.example.reglab7firebase.view.component.ContentContainerAdmin
import com.example.reglab7firebase.view.component.DialogAlert
import com.example.reglab7firebase.view.component.Loading

@Composable
fun PraktikumAsprak(
    modifier: Modifier = Modifier,
    viewModel: PraktikumAsprakViewModel = viewModel(
        factory = AppViewModelFactory(userRepo = ImplementasiUserRepo(), praktikumRepo = PraktikumRepoImpl())
    ),
    clickDetailAsprak: (String) -> Unit,
    isAdmin: Boolean,
) {
    val praktikum by viewModel.uipraktikum.collectAsStateWithLifecycle()
    val uistatus by viewModel.uistatus.collectAsStateWithLifecycle()
    val adminpraktikum by viewModel.uipraktikumAdmin.collectAsStateWithLifecycle()
    Log.d("apakah admin", isAdmin.toString())
    LaunchedEffect(Unit) {
        Log.d("apakah admin2", isAdmin.toString())
        if (isAdmin) {
            viewModel.getPrakAdmin()
        }
    }
    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (uistatus) {
                    is Cek.Error -> DialogAlert(
                        isi = (uistatus as Cek.Error).message,
                        keadaan = false,
                        confirmButon = { viewModel.statusLoading() },
                        dismisReq = { viewModel.statusLoading() }
                    )

                    Cek.Loading -> Loading()
                    Cek.Sukses -> {
                        if (isAdmin) {
                            ContentContainerAdmin(
                                praktikum = adminpraktikum,
                                clickDetail = { clickDetailAsprak(it) })
                        } else {
                            ContentContainer(
                                praktikum = praktikum,
                                clickDetail = { clickDetailAsprak(it) })
                        }

                    }

                    Cek.idle -> Loading()
                }
            }
        }
    }
}

@Preview
@Composable
private fun PraktikAsprakPrev() {

}