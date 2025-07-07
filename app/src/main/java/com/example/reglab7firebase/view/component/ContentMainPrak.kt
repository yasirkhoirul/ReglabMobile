package com.example.reglab7firebase.view.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.reglab7firebase.R
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.Praktikum
import com.example.reglab7firebase.view.dashboardAdmin.koorPraktikum.KoorPraktikumViewModel

@Composable
fun ContentKoor(
    modifier: Modifier = Modifier,
    praktikum: List<Praktikum>,
    clickDetail: (String) -> Unit,
    cllickHandling: (String, String) -> Unit,
    viewmodel: KoorPraktikumViewModel,
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
                "Belum Ada Praktikum yang dibuat",
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
                        slot = if (it.koor.isEmpty()) "Belum Ada Koor" else it.koor,
                        klikDetail = clickDetail,
                        uid = it.id_prak,
                        listAsprak = it.uid_asprak,
                        cllickHandling = cllickHandling,
                        viewmodel = viewmodel
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                }
            }

        }
    }
}


@Composable
fun ContentContainerAdmin(
    modifier: Modifier = Modifier,
    praktikum: List<Praktikum>,
    clickDetail: (String) -> Unit,
) {

    val iconPrakOrang by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.admin))
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
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                "Halo," + "Praktikum yang ada",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.size(5.dp))
        }
        HorizontalDivider()
        if (praktikum.isEmpty()) {
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                "Belum Ada Praktikum Yang dibuat",
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
                    ContentMain(
                        namaPrak = it.nama_prak,
                        slot = it.tanggal?.toDate().toString(),
                        klikDetail = clickDetail,
                        uid = it.id_prak,
                        isAdmin = true
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                }
            }
        }
    }
}

@Composable
fun ContentContainer(
    modifier: Modifier = Modifier,
    praktikum: List<Praktikum>,
    clickDetail: (String) -> Unit,
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
                "Praktikum Yang Diambil",
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
                "Belum Ada Praktikum Yang Diambil",
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
                    ContentMain(
                        namaPrak = it.nama_prak,
                        slot = it.tanggal?.toDate().toString(),
                        klikDetail = clickDetail,
                        uid = it.id_prak,
                        isAdmin = false
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                }
            }
        }
    }
}

@Composable
fun ContentMain(
    modifier: Modifier = Modifier,
    namaPrak: String,
    slot: String,
    klikDetail: (String) -> Unit,
    uid: String,
    isAdmin: Boolean,
) {
    val gambar =
        if (isAdmin) LottieCompositionSpec.RawRes(R.raw.bookadmin) else LottieCompositionSpec.RawRes(
            R.raw.prak
        )
    val iconPrak by rememberLottieComposition(gambar)
    val progres by animateLottieCompositionAsState(
        composition = iconPrak,
        iterations = LottieConstants.IterateForever
    )

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
                modifier = Modifier.size(40.dp)
            )
            Column(
                modifier = modifier,
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
            IconButton(
                onClick = { klikDetail(uid) }
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_arrow_circle_right_24),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun ContentMains(
    modifier: Modifier = Modifier,
    namaPrak: String,
    slot: String,
    klikDetail: (String) -> Unit,
    uid: String,
    listAsprak: List<String>,
    cllickHandling: (String, String) -> Unit,
    viewmodel: KoorPraktikumViewModel,
) {
    var pilihan by rememberSaveable { mutableStateOf(slot) }
    var putar by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val gambar = LottieCompositionSpec.RawRes(R.raw.bookadmin)
    val iconPrak by rememberLottieComposition(gambar)
    val progres by animateLottieCompositionAsState(
        composition = iconPrak,
        iterations = LottieConstants.IterateForever
    )


    Card(
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
        }
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
                modifier = Modifier.size(40.dp)
            )
            Column(
                modifier = modifier,
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
            IconButton(
                onClick = { putar = !putar }
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_arrow_circle_right_24),
                    contentDescription = null
                )
            }
        }
        AnimatedVisibility(
            visible = putar,
            exit = fadeOut() + shrinkVertically(),
            enter = fadeIn() + expandVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
            ) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pilih Koor",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                    IconButton(onClick = {
                        expanded = !expanded
                    }) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_keyboard_double_arrow_up_24),
                            contentDescription = null
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pilihan,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listAsprak.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = { pilihan = option }
                        )
                    }
                }
                Button(onClick = { cllickHandling(pilihan, uid) }) { Text("Ganti Koor") }
            }
        }

    }

}