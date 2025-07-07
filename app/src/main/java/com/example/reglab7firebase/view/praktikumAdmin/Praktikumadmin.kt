package com.example.reglab7firebase.view.praktikumAdmin

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.model.LottieCompositionCache
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme
import com.example.reglab7firebase.R
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.Praktikum
import com.example.reglab7firebase.data.repository.PraktikumRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.view.component.DialogAlertCancel

@Composable
fun ContentMains(
    modifier: Modifier = Modifier,
    namaPrak: String,
    slot: String,
    uid: String,
    onDelete: (String) -> Unit,
    onDetailPraktikum: (String) -> Unit,
) {
    val iconPrak by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.prak))
    val progres by animateLottieCompositionAsState(
        composition = iconPrak,
        iterations = LottieConstants.IterateForever
    )
    var klikhapus by rememberSaveable { mutableStateOf(false) }
    if (klikhapus == true){
        DialogAlertCancel(
            judul = "Peringatan", isi = "Anda yakin ingin menghapus?", confirmButon = {
                TextButton(
                    onClick = {
                        onDelete(uid)
                        klikhapus = !klikhapus
                    }
                ) { Text("confirm") }
            },
            keadaan = false,
            dismisReq = { klikhapus = !klikhapus },
            dismisBbut = {
                TextButton(
                    onClick = {
                        klikhapus = !klikhapus
                    }
                ) { Text("cancel") }
            }
        )
    }else{
        Card(
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LottieAnimation(
                    composition = iconPrak,
                    progress = { progres },
                    modifier = Modifier.size(50.dp)
                )
                Column(
                    modifier = modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        namaPrak,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Text(slot, fontSize = 11.sp)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .wrapContentWidth()
                        .weight(0.5f)
                ) {
                    IconButton(
                        onClick = {
                            onDetailPraktikum(uid)
                        }
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_arrow_circle_right_24),
                            contentDescription = null
                        )
                    }
                    Log.d("hasilnya", "uidnya adalah $uid")
                    IconButton(
                        onClick = {klikhapus = !klikhapus}
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.outline_cancel_24),
                            contentDescription = null
                        )
                    }
                }

            }
        }
    }

}

@Composable
fun ContentContainers(
    modifier: Modifier = Modifier,
    praktikum: List<Praktikum>,
    onDelete: (String) -> Unit,
    onDetailPraktikum: (String) -> Unit,
) {
    val iconPrakOrang by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.prakorang))
    val progresprakprok by animateLottieCompositionAsState(
        composition = iconPrakOrang,
        iterations = LottieConstants.IterateForever
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                composition = iconPrakOrang,
                progress = { progresprakprok },
                modifier = Modifier.size(200.dp)
            )
            Text(
                "Praktikum Yang Ada",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
        HorizontalDivider()
        if (praktikum.isEmpty()) {
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                "Belum Ada Praktikum",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(0.7f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(0.7f)
                    .padding(10.dp),
            ) {
                items(praktikum) {
                    ContentMains(
                        namaPrak = it.nama_prak,
                        slot = "...",
                        uid = it.id_prak,
                        onDelete = onDelete,
                        onDetailPraktikum = onDetailPraktikum
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                }
            }
        }
    }

}

@Composable
fun PraktikumAdmin(
    viewModel: PraktikumAdminViewModel = viewModel(
        factory = AppViewModelFactory(praktikumRepo = PraktikumRepoImpl())
    ),
    navigateTambah: () -> Unit,
    navigateDetailPrakAdmin: (String) -> Unit,
) {
    val tambah by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.add))
    val progrestambah by animateLottieCompositionAsState(
        composition = tambah,
        iterations = LottieConstants.IterateForever
    )
    val iconLoading by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.laoding))
    val progresLoading by animateLottieCompositionAsState(
        composition = iconLoading,
        iterations = LottieConstants.IterateForever
    )
    val praktikum by viewModel.uilistPraktikum.collectAsState()
    val status by viewModel.uistatus.collectAsState()

    Scaffold(
        floatingActionButtonPosition = FabPosition.Start,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navigateTambah() },
                shape = CircleShape,
                containerColor = Color.White
            ) {
                LottieAnimation(
                    composition = tambah,
                    progress = { progrestambah },
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    ) {
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
                    LottieAnimation(
                        composition = iconLoading,
                        progress = { progresLoading },
                        modifier = Modifier.size(200.dp)
                    )
                }

                Cek.Sukses -> ContentContainers(
                    praktikum = praktikum,
                    onDelete = { viewModel.getDeletePrak(it) },
                    onDetailPraktikum = {
                        navigateDetailPrakAdmin(it)
                        Log.d("hasildetaiiladmin", it)
                    })

                Cek.idle -> {
                    LottieAnimation(
                        composition = iconLoading,
                        progress = { progresLoading },
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PraktikumPrev() {
    Reglab7firebaseTheme {
        PraktikumAdmin(navigateTambah = {}, navigateDetailPrakAdmin = {})
    }
}