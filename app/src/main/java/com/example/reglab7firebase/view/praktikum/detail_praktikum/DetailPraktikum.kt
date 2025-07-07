package com.example.reglab7firebase.view.praktikum.detail_praktikum

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.reglab7firebase.data.model.DetailPertemuan
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.data.repository.PraktikumRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory

@Composable
fun Itemnya(
    modifier: Modifier = Modifier,
    pertemuan: String,
    detailPertemuan: Map<String, DetailPertemuan?>,
    expanded: Boolean,
    onClick: () -> Unit,
    id_pertemuan: String
) {
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
    LaunchedEffect(Unit) {
        if (detailPertemuan[id_pertemuan]?.status_kehadiran == false) {
            brush = Brush.horizontalGradient(
                listOf(
                    Color(0xFFE91E63),
                    Color(0xFFFFFFFF),
                    Color(0xFFFFFFFF)
                )
            )
        } else {
            brush = Brush.horizontalGradient(
                listOf(
                    Color(0xFF8BC34A),
                    Color(0xFFFFFFFF),
                    Color(0xFFFFFFFF)
                )
            )
        }
    }
    Card(
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
                    progress = {meetprogre},
                    modifier = Modifier.size(90.dp)
                )
                Text(pertemuan)

                IconButton(
                    onClick = onClick,
                ) {
                    Icon(
                        contentDescription = null,
                        imageVector = ImageVector.vectorResource(R.drawable.outline_next_plan_24),
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
            val nilai = detailPertemuan[id_pertemuan]?.nilai
            val kehadiran = detailPertemuan[id_pertemuan]?.status_kehadiran
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color.White)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    imageVector = if (kehadiran == true) ImageVector.vectorResource(R.drawable.outline_check_circle_24) else ImageVector.vectorResource(
                        R.drawable.outline_cancel_24
                    ), contentDescription = null, modifier = Modifier.size(100.dp)
                )
                Text("Nilai :")
                Text(nilai.toString())
                HorizontalDivider()
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPraktikum(
    modifier: Modifier = Modifier,
    uid: String,
    viewModel: DetailPraktikumViewModel = viewModel(
        factory = AppViewModelFactory(
            praktikumRepo = PraktikumRepoImpl(),
            userRepo = ImplementasiUserRepo()
        )
    ),
) {
    LaunchedEffect(Unit) {
        viewModel.getPertemuanUser(uid)
    }

    val uidUser by viewModel.uiUiduser.collectAsStateWithLifecycle()
    val pertemuan by viewModel.uiPertemuanPraktikumState.collectAsState()
    val detailpertemuan by viewModel.uidetailpertemuan.collectAsStateWithLifecycle()
    var indexs by rememberSaveable { mutableStateOf<Int?>(null) }
    Log.d("hasilDetail", uid)
    Scaffold(
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .padding(5.dp), contentAlignment = Alignment.TopStart
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    uid,
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
                if (pertemuan.isNotEmpty()){
                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(),
                        contentPadding = PaddingValues(1.dp)
                    ) {
                        itemsIndexed(pertemuan) { index, it ->
                            Itemnya(
                                id_pertemuan = it.id_pertemuan,
                                pertemuan = it.nama_pertemuan,
                                detailPertemuan = detailpertemuan,
                                expanded = if (indexs == index) true else false,
                                onClick = {
                                    indexs = if (indexs == index) {
                                        null
                                    } else {
                                        index
                                    }

                                })
                            Spacer(modifier = Modifier.size(5.dp))
                        }

                    }
                }else{
                    Text("Belum Ada Pertemuan")
                }

            }

        }
    }

}
