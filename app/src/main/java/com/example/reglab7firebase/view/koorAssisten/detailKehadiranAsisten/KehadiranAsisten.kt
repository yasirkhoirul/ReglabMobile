package com.example.reglab7firebase.view.koorAssisten.detailKehadiranAsisten

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reglab7firebase.data.model.Dummy
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme
import com.example.reglab7firebase.view.koorAssisten.Item
import com.example.reglab7firebase.view.koorAssisten.KoorAssisten

@Composable
fun DetailAsisten(modifier: Modifier = Modifier) {
    val item = Dummy().isinyas
    Scaffold {
        Box(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.size(5.dp))
                Text("Detail Asisten", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(5.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth(0.6f))
                LazyColumn(contentPadding = PaddingValues(10.dp), modifier = Modifier.fillMaxSize()) {
                    items(item) {
                        Item(namaPraktikum = it.judul, isDetail = false, onClick = {}, item = item)
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }

            }
        }
    }
}

@Preview
@Composable
private fun DetailAsistenPPrev() {
    Reglab7firebaseTheme {
        DetailAsisten()
    }
}