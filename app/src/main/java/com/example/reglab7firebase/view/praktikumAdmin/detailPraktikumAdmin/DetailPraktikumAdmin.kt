package com.example.reglab7firebase.view.praktikumAdmin.detailPraktikumAdmin

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.reglab7firebase.R
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.Praktikum
import com.example.reglab7firebase.data.model.User
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.data.repository.PraktikumRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.view.component.DialogAlert
import com.example.reglab7firebase.view.component.Elemenair
import com.example.reglab7firebase.view.component.Loading
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ContentAsprak(
    modifier: Modifier = Modifier,
    nim: String,
    email: String,
    detail: String,
    klikHapus: (String) -> Unit,
) {
    var putar by rememberSaveable { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (putar) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "rotation"
    )
    Card(
        onClick = { putar = !putar }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4CAF50))
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    nim,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_keyboard_double_arrow_up_24),
                    contentDescription = null,
                    modifier = Modifier.rotate(rotationAngle)
                )
            }
            AnimatedVisibility(
                visible = putar,
                exit = fadeOut() + shrinkVertically(),
                enter = fadeIn() + expandVertically()
            ) {
                Column {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = email,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )
                    Button(onClick = { klikHapus(detail) }) { Text("Hapus") }
                }
            }
        }
    }
    Spacer(modifier = Modifier.size(5.dp))
}

@Preview(showBackground = true)
@Composable
private fun ContentPrev() {
    Reglab7firebaseTheme {
        ContentAsprak(
            nim = "21000023",
            email = "sbdabdkajsdkajsdjabsjdbakjsdbasdsbdabdkajsdkajsdjabsjdbakjsdbasd",
            detail = "",
            klikHapus = {})
    }
}

@Composable
fun Asprak(
    modifier: Modifier = Modifier,
    asprak: List<User>,
    klik: () -> Unit,
    nama: String,
    klikFab: () -> Unit,
    detail: String,
    klikHapus: (String) -> Unit,
) {
    var putar by rememberSaveable { mutableStateOf(false) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {

        Card(onClick = {
            klik()
            putar = !putar
        }, elevation = CardDefaults.cardElevation(5.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(10.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                val rotationAngle by animateFloatAsState(
                    targetValue = if (putar) 180f else 0f,
                    animationSpec = tween(durationMillis = 300),
                    label = "rotation"
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_keyboard_double_arrow_up_24),
                            contentDescription = null,
                            modifier = Modifier
                                .rotate(rotationAngle)
                        )
                        Text(nama, fontWeight = FontWeight.Bold)
                    }
                    if (asprak.isEmpty()) {
                        Text(
                            "Tidak Ada $nama", modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.CenterHorizontally), textAlign = TextAlign.Center
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxSize()
                        ) {
                            items(asprak) {
                                ContentAsprak(
                                    nim = it.nim,
                                    email = it.email,
                                    detail = it.uid,
                                    klikHapus = klikHapus
                                )
                            }
                        }
                    }

                }

                FloatingActionButton(
                    containerColor = Color.White,
                    onClick = klikFab,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) { Text("Tambah $nama", modifier = Modifier.padding(5.dp)) }
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
private fun AsprakPrev() {
    Reglab7firebaseTheme {
        Asprak(
            asprak = listOf(
                User("uid", "email", "password", "role", "1232321"),
                User("uid1", "email1", "password1", "rol1", "3242342334"),
                User("uid1", "email1", "password1", "rol1", "3242342")
            ),
            klik = {},
            nama = "agus",
            klikFab = {},
            klikHapus = {},
            detail = ""
        )
    }
}

@Composable
fun Container(modifier: Modifier = Modifier, detailPraktikum: Praktikum?) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            detailPraktikum?.nama_prak ?: "gagal",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(10.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.size(10.dp))
        Card(elevation = CardDefaults.cardElevation(5.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(Color.White)
                    .wrapContentHeight()
                    .padding(20.dp)
            ) {
                Rows(
                    kiri = "Id Praktikum :",
                    kanan = detailPraktikum?.id_prak ?: "gagal"
                )
                Rows(
                    kiri = "Tanggal Mulai :", kanan = formatFirestoreTimestamp(
                        detailPraktikum?.tanggal
                    )
                )
                Rows(
                    kiri = "Jumlah Pertemuan",
                    kanan = "${detailPraktikum?.jumlahPertemuan}"
                )
            }
        }
        Spacer(modifier = Modifier.size(10.dp))
    }
}

@Composable
fun Rows(modifier: Modifier = Modifier, kiri: String, kanan: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(kiri)
        Text(kanan)
    }
}

fun formatFirestoreTimestamp(timestamp: Timestamp?): String {
    if (timestamp == null) {
        // Jika timestamp null, kembalikan string default atau tangani sesuai kebutuhan
        return "Tanggal tidak tersedia"
    }

    val date: Date =
        timestamp.toDate() // Di sini, kita yakin date tidak null karena sudah diperiksa di atas
    val formatter = SimpleDateFormat("dd MMMM, HH:mm", java.util.Locale("id", "ID"))
    return formatter.format(date)
}

enum class PanelState {
    NONE, ASPRAK, MAHASISWA
}

@Composable
fun DetailPrakAdmin(
    modifier: Modifier = Modifier,
    id_prak: String,
    viewModel: DetailPraktikumAdminViewModel = viewModel(
        factory = AppViewModelFactory(
            userRepo = ImplementasiUserRepo(),
            praktikumRepo = PraktikumRepoImpl()
        )
    ),
    navigaTeSarching: (String, Boolean) -> Unit,
) {
    val detailPraktikum by viewModel.uiPraktikumDetail.collectAsState()
    Log.d("detailprakj", detailPraktikum.toString())
    val status by viewModel.uistatus.collectAsState()
    Log.d("status", status.toString())
    val listAsprak by viewModel.uilistAsprakCurrent.collectAsStateWithLifecycle()
    Log.d("hasilas", " uinya $listAsprak")
    val listMahasiswa by viewModel.uiListMahasiswaCurrent.collectAsStateWithLifecycle()

    var activePanel by rememberSaveable { mutableStateOf(PanelState.NONE) }
    val asprakExpanded by remember { derivedStateOf { activePanel == PanelState.ASPRAK } }
    val mahasiswaExpanded by remember { derivedStateOf { activePanel == PanelState.MAHASISWA } }

    val asprakScale by animateFloatAsState(
        targetValue = if (asprakExpanded) 1f else 1f,
        animationSpec = spring(),
        label = "asprakScale"
    )

    val mahasiswaScale by animateFloatAsState(
        targetValue = if (mahasiswaExpanded) 1f else 0.5f,
        animationSpec = spring(),
        label = "mahasiswaScale"
    )
    LaunchedEffect(Unit) {
        viewModel.getUser(id_prak)
    }
    Scaffold {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        )
        {
            Elemenair(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.BottomStart), arah = true
            )
            Elemenair(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.TopEnd), arah = false
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (status) {
                    is Cek.Error -> DialogAlert(
                        isi = (status as Cek.Error).message,
                        keadaan = false,
                        dismisReq = { viewModel.GoIdle() },
                        confirmButon = {
                            TextButton(
                                onClick = { viewModel.GoIdle() }
                            ) { Text("Confirm") }
                        })

                    Cek.Loading -> Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Loading()
                    }

                    Cek.idle -> {
                        AnimatedVisibility(visible = activePanel == PanelState.NONE) {
                            Container(
                                detailPraktikum = detailPraktikum,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        AnimatedVisibility(visible = activePanel == PanelState.NONE || activePanel == PanelState.MAHASISWA) {
                            Asprak(

                                detail = detailPraktikum?.id_prak.toString(),
                                asprak = listMahasiswa,
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .fillMaxHeight(mahasiswaScale),
                                klik = {
                                    activePanel =
                                        if (activePanel == PanelState.MAHASISWA) PanelState.NONE else PanelState.MAHASISWA
                                },
                                nama = "Mahasiswa",
                                klikFab = {
                                    navigaTeSarching(
                                        detailPraktikum?.id_prak ?: "Tidak ada ID", false
                                    )
                                },
                                klikHapus = {
                                    viewModel.getDeleteMahasiswa(
                                        it,
                                        detailPraktikum?.id_prak ?: ""
                                    )
                                }
                            )
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                        AnimatedVisibility(visible = activePanel == PanelState.NONE || activePanel == PanelState.ASPRAK) {
                            Asprak(
                                detail = detailPraktikum?.id_prak.toString(),
                                asprak = listAsprak,
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .fillMaxHeight(asprakScale),
                                klik = {
                                    activePanel =
                                        if (activePanel == PanelState.ASPRAK) PanelState.NONE else PanelState.ASPRAK
                                },
                                nama = "Asprak",
                                klikFab = {
                                    navigaTeSarching(
                                        detailPraktikum?.id_prak ?: "Tidak ada ID", true
                                    )
                                },
                                klikHapus = {
                                    viewModel.getDeleteAsprak(
                                        uid = it,
                                        idPrak = detailPraktikum?.id_prak ?: ""
                                    )
                                }
                            )
                        }
                    }

                    Cek.Sukses -> {
                        DialogAlert(
                            isi = "Berhasil",
                            keadaan = true,
                            dismisReq = {},
                            confirmButon = {
                                TextButton(
                                    onClick = { viewModel.GoIdle() }
                                ) { Text("Confirm") }
                            })
                        Log.d("hasilas", "terhapus")
                    }
                }
            }
        }
    }
}
