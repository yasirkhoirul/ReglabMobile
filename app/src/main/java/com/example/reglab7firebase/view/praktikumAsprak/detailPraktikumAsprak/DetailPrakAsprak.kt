package com.example.reglab7firebase.view.praktikumAsprak.detailPraktikumAsprak

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.reglab7firebase.R
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.DetailPertemuan
import com.example.reglab7firebase.data.model.DetailPertemuanLengkap
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.data.repository.PraktikumRepoImpl
import com.example.reglab7firebase.data.repository.TimeRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.view.component.DialogAlert
import com.example.reglab7firebase.view.component.DialogForm
import com.example.reglab7firebase.view.component.Loading
import com.google.firebase.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPraktikumAsprak(
    uid: String,
    viewModel: DetailAsprakViewModel = viewModel(
        factory = AppViewModelFactory(
            timeRepo = TimeRepoImpl(),
            praktikumRepo = PraktikumRepoImpl(),
            userRepo = ImplementasiUserRepo()
        )
    ),
    generateqr: (String, String) -> Unit,
) {
    val listPertemuan by viewModel.uipertemuanPrak.collectAsStateWithLifecycle()
    Log.d("dibuka", "uinya $listPertemuan")
    val detailPertemeuan by viewModel.uidetailPertemuan.collectAsStateWithLifecycle()
    val nilaistate by viewModel.uiStateUpdate.collectAsStateWithLifecycle()
    val uistatus by viewModel.uistatus.collectAsStateWithLifecycle()
    val open by viewModel.uiopen.collectAsStateWithLifecycle()
    val role by viewModel.uiloginState.collectAsStateWithLifecycle()
    val statuskehadiran by viewModel.uistatuskehadiran.collectAsStateWithLifecycle()
    Log.d("dibuka", "$open")

    LaunchedEffect(Unit) {
        viewModel.getPertemuan(uid)
    }
    var indexs by rememberSaveable { mutableStateOf<Int?>(null) }

    Log.d("dibuka", "$open")
    Log.d("hasilDetail", uid)
    Scaffold(
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .padding(5.dp), contentAlignment = Alignment.TopStart
        ) {
            if (uistatus is Cek.Sukses) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    viewModel.changeStatusKehadiran(false)
                    viewModel.updateNilai("")
                    DialogAlert(
                        isi = "Sukses",
                        keadaan = true,
                        confirmButon = {
                            Button(onClick = { viewModel.goIdle() }) { Text("Ok") }
                        },
                        dismisReq = {}
                    )
                }
            } else if (uistatus is Cek.Loading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Loading()
                }
            } else if (uistatus is Cek.Error) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DialogAlert(
                        isi = (uistatus as Cek.Error).message.toString(),
                        keadaan = false,
                        confirmButon = {
                            Button(onClick = { viewModel.goIdle() }) { Text("Ok") }
                        },
                        dismisReq = {}
                    )
                }
            }
            if (open == true) {
                DialogForm(
                    judul = "Silahkan Masukkan Nilai",
                    isi = nilaistate.nilai.toString(),
                    confirmButon = {
                        TextButton(onClick = {
                            viewModel.subbitHandling()
                            viewModel.closDialog()
                        }) { Text("OK") }
                    },
                    dismisReq = { },
                    dismisBbut = {
                        TextButton(onClick = {
                            viewModel.closDialog()
                        }) {
                            Text("Cancel")
                        }
                    },
                    onValueChange = { viewModel.updateNilai(it) },
                    checked = statuskehadiran,
                    onchangeCHeck = {viewModel.changeStatusKehadiran(it)}
                )

            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Halo,",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Selamat Datang",
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider()
                Spacer(modifier = Modifier.size(10.dp))

                //lazy column
                if (listPertemuan.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(),
                        contentPadding = PaddingValues(1.dp)
                    ) {
                        itemsIndexed(listPertemuan) { index, it ->
                            Log.d("dibuka", " dalam lazy ${it.detail_pertemuan}")
                            ItemnyaAsprak(
                                //it tanggal > tanggal skrang
                                viewModel = viewModel,
                                tanggal = it.tanggal,
                                pertemuan = it.nama_pertemuan,
                                detailPertemuan = detailPertemeuan,
                                expanded = if (indexs == index) true else false,
                                onClick = {
                                    if (indexs == index) {
                                        indexs = null
                                        Log.d("dibuka", "tutup")
                                    } else {
                                        viewModel.getDetailMahasiswa(it.detail_pertemuan)
                                        indexs = index
                                        Log.d(
                                            "dibuka",
                                            "indexnya ${indexs} idpertnya ${it.id_pertemuan} "
                                        )
                                    }
                                },
                                edit = { idmhs ->
                                    Log.d("dibuka", "open")
                                    viewModel.openDialog()
                                    viewModel.onUpdateData(
                                        idPrak = uid,
                                        idPertemuan = it.id_pertemuan,
                                        idMahasiswa = idmhs.uid_mahasiswa,
                                        data = idmhs
                                    )
                                },
                                generateQr = {
                                    generateqr(uid, it.id_pertemuan)
                                },
                                isAdmin = if(role?.role == "admin") true else false
                            )
                            Spacer(modifier = Modifier.size(5.dp))
                        }

                    }
                } else {
                    Text("Belum Ada Pertemuan")
                }
            }
        }
    }
}

@Composable
fun ItemnyaAsprak(
    viewModel: DetailAsprakViewModel,
    isAdmin : Boolean,
    modifier: Modifier = Modifier,
    tanggal: Timestamp,
    pertemuan: String,
    detailPertemuan: List<DetailPertemuanLengkap>,
    expanded: Boolean,
    onClick: () -> Unit,
    edit: (DetailPertemuan) -> Unit,
    generateQr: () -> Unit,
) {
    var cek by remember { mutableStateOf(false) }
    val compossistionmeet by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.meet))
    val meetprogre by animateLottieCompositionAsState(
        composition = compossistionmeet,
        iterations = LottieConstants.IterateForever
    )
    var brush by remember {
        mutableStateOf<Brush>(
            Brush.horizontalGradient(
                listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFFFFFFF)
                )
            )
        )
    }
    Card(
        onClick = {},
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = brush
                )
                .padding(5.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LottieAnimation(
                    composition = compossistionmeet,
                    progress = { meetprogre },
                    modifier = Modifier.size(90.dp)
                )
                Text(pertemuan)

                IconButton(
                    onClick = {
                        generateQr()
                    },
//                    generateQr
                ) {
                    Icon(
                        contentDescription = null,
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_qr_code_24),
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(
                    onClick = onClick,
                ) {
                    Icon(
                        contentDescription = null,
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_keyboard_double_arrow_up_24),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.White)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp),
                    contentPadding = PaddingValues(10.dp)
                ) {
                    items(detailPertemuan) {
                        val warna= if (!it.detailPertemuan.status_kehadiran){
                            Brush.linearGradient(listOf(Color.White, Color.White,Color.Red))
                        }else{
                            Brush.linearGradient(listOf(Color.White, Color.White,Color.White))
                        }
                        Card(elevation = CardDefaults.elevatedCardElevation(5.dp)) {
                            Column(
                                modifier = Modifier
                                    .background(brush = warna)
                                    .padding(10.dp)
                                    .fillMaxWidth()
                            ) {
                                Text("nim = ${it.nim}")
                                Text("nilai =${it.detailPertemuan.nilai}")
                                Text("kehadiran =${if (it.detailPertemuan.status_kehadiran) "hadir" else "tidak hadir"}")
                                Button(
                                    onClick = {
                                        edit(it.detailPertemuan)
                                    },
                                    modifier = Modifier.align(Alignment.End),
                                    enabled = if (isAdmin)true else it.detailPertemuan.status_kehadiran
                                ) {
                                    Text("Edit")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
