package com.example.reglab7firebase.view.praktikum.praktikumTerdekat

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.Dummy
import com.example.reglab7firebase.data.model.Praktikum
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.data.repository.PraktikumRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme
import com.example.reglab7firebase.view.component.DialogAlert
import com.example.reglab7firebase.view.component.Elemenair
import com.example.reglab7firebase.view.component.GradientButton
import com.example.reglab7firebase.view.component.Loading
import com.example.reglab7firebase.view.component.Pink


@Composable
fun CircularIndicator(
    value: Int,
    maxValue: Int,
    modifier: Modifier = Modifier.size(150.dp),
) {
    val progress = (value / maxValue.toFloat()) * 260f

    // 2. Buat Brush menggunakan SweepGradient
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size
            val center = Offset(canvasSize.width / 2, canvasSize.height / 3)
            val gradientColors = Brush.sweepGradient(
                listOf(Color(0xFF4CAF50), Color(0xFF8BC34A)),
                center = Offset(canvasSize.width / 2, canvasSize.height / 1)
            )
            // Titik tengah canvas
            // Background circle
            drawArc(
                color = Color.LightGray,
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                style = Stroke(40f, cap = StrokeCap.Round)
            )

            // Progress arc
            drawArc(
                brush = gradientColors,
                startAngle = 140f,
                sweepAngle = progress,
                useCenter = false,
                style = Stroke(40f, cap = StrokeCap.Round),
            )
        }
        Text(
            text = "$value / $maxValue",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun PraktikumTerdekat(
    modifier: Modifier = Modifier,
    viewModel: PraktikumTerdekatViewModel = viewModel(
        factory = AppViewModelFactory(
            praktikumRepo = PraktikumRepoImpl(),
            userRepo = ImplementasiUserRepo()
    )),
    onDetail:(String)-> Unit
) {
    val status by viewModel.uistatus.collectAsStateWithLifecycle()
    Log.d("status prkterdetkat",status.toString())
    val jadwal by viewModel.uiJadwal.collectAsStateWithLifecycle()
    Scaffold {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Elemenair(arah = false)
            Elemenair(arah = true)
            Column(modifier = Modifier.fillMaxWidth()) {
                when (status) {
                    is Cek.Error -> {
                        DialogAlert(
                            isi = (status as Cek.Error).message,
                            keadaan = false,
                            confirmButon = {
                                TextButton(
                                    onClick = { viewModel.down() }
                                ) { Text("OK") }
                            },
                            dismisReq = { viewModel.down() },
                        )
                    }

                    Cek.Loading ->
                        Card(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Column(
                                modifier = Modifier
                                    .size(200.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Loading()
                            }
                        }

                    Cek.Sukses, Cek.idle -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .wrapContentHeight()
                        ) {
                            Text(
                                "Halo, Berikut Praktikum Terdekatmu!",
                                fontSize = 50.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                lineHeight = 50.sp
                            )
                        }
                        if (jadwal.isEmpty()) {
                            Box (modifier = Modifier.fillMaxSize(1f)) {
                                Text(
                                    "Tidak Ada Praktikum Terdekat Yey!", textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp, modifier = Modifier.fillMaxWidth().align(Alignment.Center)
                                )
                            }
                        }
                        LazyRow(
                            modifier = Modifier.fillMaxSize(1f),
                            contentPadding = PaddingValues(40.dp)
                        ) {
                            items(jadwal) {
                                Card(elevation = CardDefaults.elevatedCardElevation(10.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight(1f)
                                            .width(300.dp)
                                            .background(
                                                brush = Brush.linearGradient(
                                                    listOf(
                                                        Color(0xFF0284C7),
                                                        Color(0xFFFFFFFF),
                                                        Color(0xFFFFFFFF)
                                                    )
                                                )
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxHeight(1f)
                                                .width(300.dp)
                                                .padding(20.dp)
                                        ) {
                                            Text(
                                                it.namaPrak, textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp, modifier = Modifier.fillMaxWidth()
                                            )
                                            Text(
                                                it.nama_pertemuan, textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 15.sp, modifier = Modifier.fillMaxWidth()
                                            )
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                CircularIndicator(
                                                    value = it.jumlahmask,
                                                    maxValue = it.jumlahpertemuan
                                                )
                                                Spacer(modifier = Modifier.height(16.dp))
                                                Text(
                                                    text = "Jumlah Kehadiran Kamu",
                                                    textAlign = TextAlign.Center,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )
                                                Spacer(modifier = Modifier.height(16.dp))
                                                Text(
                                                    text = "Mulai Pada",
                                                    textAlign = TextAlign.Center,
                                                    fontWeight = FontWeight.Thin,
                                                    fontSize = 16.sp
                                                )
                                                Text(
                                                    text = it.tanggal.toDate().toString(),
                                                    textAlign = TextAlign.Center,
                                                    fontWeight = FontWeight.Thin,
                                                    fontSize = 12.sp
                                                )
                                                Spacer(modifier = Modifier.height(16.dp))
                                                GradientButton(
                                                    text = "Lihat",
                                                    gradient = Brush.horizontalGradient(
                                                        listOf(
                                                            Color(
                                                                0xFF81D4FA
                                                            ), Color(0xFF005CB2)
                                                        )
                                                    ),
                                                    modifier = Modifier
                                                        .width(100.dp)
                                                        .height(50.dp),
                                                    onClick = {onDetail(it.id_prak)},
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.size(50.dp))
                            }
                        }
                    }
                }

            }
        }
    }
}

@Preview
@Composable
private fun PraktikumTerdekatPrev() {
    Reglab7firebaseTheme {
        PraktikumTerdekat(onDetail = {})
    }
}