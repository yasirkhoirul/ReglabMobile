package com.example.reglab7firebase.view.dashboardAdmin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reglab7firebase.view.component.DrawableNavigationAdmin
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.reglab7firebase.R
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.repository.UserRepo
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.data.repository.InfoRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.view.component.DialogAlert
import com.example.reglab7firebase.view.component.DialogForm
import com.example.reglab7firebase.view.component.Loading

@Composable
fun DialogFormAddInformasi(
    judul: String,
    judulisi: String,
    onjudulChange:(String) -> Unit,
    isi: String,
    onValueChange: (String) -> Unit,
    confirmButon: @Composable () -> Unit,
    dismisReq: () -> Unit,
    dismisBbut: @Composable () -> Unit,

) {
    val tambah by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.add))
    val progresFail by animateLottieCompositionAsState(
        composition = tambah,
        iterations = LottieConstants.IterateForever
    )
    AlertDialog(
        icon = {
            LottieAnimation(
                composition = tambah,
                progress = { progresFail },
                modifier = Modifier.size(80.dp)
            )
        },
        title = {
            Text(judul)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = judulisi,
                    onValueChange = { onjudulChange(it) },
                    label = { Text("judul") }
                )
                OutlinedTextField(
                    value = isi,
                    onValueChange = { onValueChange(it) },
                    label = { Text("isi") }
                )

            }
        },
        confirmButton = confirmButon,
        onDismissRequest = dismisReq,
        dismissButton = dismisBbut
    )
}

@Composable
fun DashboardAdmin(
    viewModel: DashBoardAdminViewModel = viewModel( factory = AppViewModelFactory(userRepo = ImplementasiUserRepo(), infoRepo = InfoRepoImpl()) ),
    signOut: () -> Unit,
    navigateDetail: (String) -> Unit,
    klikPrak: () -> Unit,
    klikTambah: () -> Unit,
    klikKoor: () -> Unit,
    kliktitik: () -> Unit,
) {
    val user by viewModel.uiUser.collectAsStateWithLifecycle()
    val informasi by viewModel.uiinformasi.collectAsStateWithLifecycle()
    val status by viewModel.uistatus.collectAsStateWithLifecycle()
    val open by viewModel.uiopen.collectAsStateWithLifecycle()
    val informasinya by viewModel.uiUpdate.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.uiTriggerOut.collect {
            signOut()
        }
    }
    Box(modifier = Modifier.statusBarsPadding(), contentAlignment = Alignment.Center){
        if (status is Cek.Loading){
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Loading()
            }
        }else if(status is Cek.Error){
            Text((status as Cek.Error).message)
        }
        else{
            DrawableNavigationAdmin(
                signOut = signOut,
                nama = user?.email ?: "memuat..",
                nim = user?.role ?: "memuat...",
                isiinformasi = informasi,
                klikIsi = navigateDetail,
                klikPrak = klikPrak,
                toTambahPrak = klikTambah,
                klikKoor = klikKoor,
                kliktitik = kliktitik,
                tambahInfor = {viewModel.openDialog()},
                deleteInfor = {viewModel.hapusInfor(it)}
            )
            if (open == true){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    DialogFormAddInformasi(
                        judul = "Tambah Informasi",
                        judulisi = informasinya.judul,
                        onjudulChange = { viewModel.judulUpdate(it) },
                        isi = informasinya.isi,
                        onValueChange = { viewModel.isilUpdate(it) },
                        confirmButon = { Button(onClick = {viewModel.tambahinformasi()}) { Text("OK") } },
                        dismisReq = { viewModel.closDialog() },
                        dismisBbut = {  Button(onClick = {viewModel.closDialog()}) { Text("Keluar")} }
                    )
                }

            }
        }
    }
}