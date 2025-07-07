package com.example.reglab7firebase.view.dashboard.detailinformasi

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme
import com.example.reglab7firebase.ui.theme.abuabu
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import com.example.reglab7firebase.data.repository.InfoRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory

@Composable
fun Detalindormasi(
    modifier: Modifier = Modifier,
    uid: String,
    viewModel: DetailViewModel = viewModel(factory = AppViewModelFactory(infoRepo = InfoRepoImpl())),
) {
    val hasil = viewModel.uIuidstate.collectAsState()
    val status by viewModel.uistatus.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.Getdetail(uid)
    }
    Scaffold {
        if (status is Cek.Sukses) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .padding(15.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    hasil.value?.judul ?: "Coba Lagi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider()
                Text(hasil.value?.isi ?: "Coba Lagi")
            }
        } else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }

    }
}

@Preview
@Composable
private fun DetailIndormasiPrev() {
    Reglab7firebaseTheme {
        Detalindormasi(uid = "asdasdasd")
    }
}