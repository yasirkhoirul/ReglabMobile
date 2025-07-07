package com.example.reglab7firebase.view.koorAssisten

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme
import com.example.reglab7firebase.R
import com.example.reglab7firebase.data.model.Dummy
import com.example.reglab7firebase.data.model.Isi
import com.example.reglab7firebase.view.component.Elemenair

@Composable
fun Item(modifier: Modifier = Modifier, namaPraktikum: String,isDetail: Boolean,onClick:()-> Unit,item: List<Isi>) {
    var buka by rememberSaveable { mutableStateOf(false) }
    Card(elevation = CardDefaults.cardElevation(10.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
            contentAlignment = Alignment.Center
        ) {
            if (isDetail) Elemenair(arah = true)
            Elemenair(arah = false)
            Column (horizontalAlignment = Alignment.CenterHorizontally){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!isDetail) Image(imageVector = ImageVector.vectorResource(R.drawable.baseline_person_24),contentDescription = null)
                    if (!isDetail) Spacer(modifier = Modifier.size(5.dp))
                    Text(namaPraktikum, textAlign = TextAlign.Start, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    val gambar = if (isDetail)R.drawable.baseline_arrow_circle_right_24 else R.drawable.baseline_keyboard_double_arrow_up_24
                    IconButton(onClick = {
                        buka = !buka
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(gambar),
                            contentDescription = null
                        )
                    }

                }
                if (!isDetail){
                    if (buka){
                        Spacer(modifier = Modifier.size(5.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.size(5.dp))
                        Column (modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                            Text("12", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                            item.map {
                                Text(it.judul)
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun KoorAssisten(modifier: Modifier = Modifier) {
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
                Text("Koor Asisten", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(5.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth(0.6f))
                LazyColumn(contentPadding = PaddingValues(10.dp), modifier = Modifier.fillMaxSize()) {
                    items(item) {
                        Item(namaPraktikum = it.judul, isDetail = true, onClick = {}, item = item)
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Koorasistenprev() {
    Reglab7firebaseTheme {
        KoorAssisten()
    }
}