package com.example.reglab7firebase.view.praktikum

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.data.repository.PraktikumRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.view.component.ContentContainer
import com.example.reglab7firebase.view.component.ContentMain
import com.example.reglab7firebase.view.component.Loading

@Composable
fun Praktikum(
    viewModel: PraktikumViewModel = viewModel(
        factory = AppViewModelFactory(
            praktikumRepo = PraktikumRepoImpl(),
            userRepo = ImplementasiUserRepo()
        )
    ),
    clickDetail: (String) -> Unit,
) {
    val iconLoading by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.laoding))
    val progresLoading by animateLottieCompositionAsState(
        composition = iconLoading,
        iterations = LottieConstants.IterateForever
    )
    val praktikum by viewModel.uiPraktikumUserState.collectAsState()
    val status by viewModel.uicek.collectAsState()

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

                Cek.Sukses -> ContentContainer(praktikum = praktikum, clickDetail = clickDetail)
                Cek.idle -> {
                    Loading()
                }
            }
        }
    }
}

@Preview
@Composable
private fun PraktikumPrev() {
    Reglab7firebaseTheme {
        Praktikum()
    }
}