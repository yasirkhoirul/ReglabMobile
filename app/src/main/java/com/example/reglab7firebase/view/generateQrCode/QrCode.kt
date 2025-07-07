package com.example.reglab7firebase.view.generateQrCode

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.data.repository.PraktikumRepoImpl
import com.example.reglab7firebase.data.repository.TimeRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.util.AndroidQrCodeGenerator
import com.example.reglab7firebase.view.component.DialogAlert
import com.example.reglab7firebase.view.component.Loading
import com.google.firebase.Timestamp
import java.util.Date

@Composable
fun PrevQr(
    modifier: Modifier = Modifier,
    viewModel: QRCodeViewModel = viewModel(
        factory = AppViewModelFactory(
            userRepo = ImplementasiUserRepo(),
            praktikumRepo = PraktikumRepoImpl(),
            timeRepo = TimeRepoImpl(),
            qrCodeGenerator = AndroidQrCodeGenerator()
        )
    ),
    idPrak: String,
    idPertemuan: String,
) {
    LaunchedEffect(Unit) {
        viewModel.getDetailPertemuan(idPrak, idPertemuan)
    }
    val pertemuan by viewModel.uidetailPertemuan.collectAsStateWithLifecycle()
    val qrBitmap by viewModel.qrCodeBitmap.collectAsState()
    val waktu by viewModel.uiwaktu.collectAsStateWithLifecycle()
    Log.d("waktu", pertemuan?.tanggal.toString())
    val status by viewModel.uistatus.collectAsStateWithLifecycle()
    val role by viewModel.uiloginState.collectAsStateWithLifecycle()

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (status) {
                is Cek.Error -> {
                    DialogAlert(
                        isi = (status as Cek.Error).message,
                        keadaan = false,
                        confirmButon = { TextButton(onClick = { viewModel.getIdle() }) { Text("OK") } },
                        dismisReq = {}
                    )
                }

                Cek.Loading -> {
                    Loading()
                }

                Cek.Sukses -> {
                    Text(pertemuan?.nama_pertemuan ?: "Memuat...")
                    val waktusekarang = waktu?.toDate()?.time
                    var waktu1menit = waktu?.toDate()?.time?.plus(900000)
                    Text(
                        "Kode Berlaku dari ${Date(waktusekarang ?: 0)}",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Text("Hingga ${Date(waktu1menit ?: 0)}", textAlign = TextAlign.Center)


                    qrBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "QR Code Pengguna",
                            modifier = Modifier.size(250.dp)
                        )
                    } ?: run {
                        //gak ada apa apa
                    }

                    Button(onClick = {
                        viewModel.onGetServerTimeClicked(
                            idPrak = idPrak,
                            idPertemuan = idPertemuan,
                            waktuprak = pertemuan?.tanggal,
                            isAdmin = if (role?.role == "admin") true else false
                        )
                    }) { Text("Generate Again") }
                }

                Cek.idle -> {
                    Text(pertemuan?.nama_pertemuan ?: "Memuat...")
                    Button(onClick = {
                        viewModel.onGetServerTimeClicked(
                            idPrak = idPrak,
                            idPertemuan = idPertemuan,
                            waktuprak = pertemuan?.tanggal,
                            isAdmin = if (role?.role == "admin") true else false
                        )
                    }) { Text("Generate") }
                }
            }
        }
    }

}