package com.example.reglab7firebase.view.tambahDataPraktikum

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.reglab7firebase.R
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.repository.PraktikumRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme
import com.example.reglab7firebase.util.AndroidTimeProvider
import com.example.reglab7firebase.view.component.Loading
import java.util.Date
import java.util.Locale

@Composable
fun Content(
    value: String,
    onValueChanges: (String) -> Unit,
    nama: String
) {
    Column (
        modifier = Modifier.width(250.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(nama, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChanges,
            label = { Text(nama) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContohTimePicker(
    jam: Int?,
    menit: Int?,
    changeJam: (Int?) -> Unit,
    changeMenit: (Int?) -> Unit,
) {
    val calendar = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = calendar.get(Calendar.MINUTE),
        is24Hour = true // Gunakan format 24 jam
    )
    var showDialog by remember { mutableStateOf(false) }
    var selectedTimeText by remember { mutableStateOf("") }

    selectedTimeText =
        if (jam == null || menit == null) "" else String.format("%02d:%02d", jam, menit)

    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { showDialog = true }) {
            Text("Pilih Waktu")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Waktu yang dipilih: $selectedTimeText")
    }
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(shape = RoundedCornerShape(16.dp)) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Pilih Waktu", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(24.dp))
                    TimePicker(state = timePickerState)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Batal")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                showDialog = false
                                changeJam(timePickerState.hour)
                                changeMenit(timePickerState.minute)
                            }
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContohDatePicker(tanggal: Long?, changeTanggal: (Long?) -> Unit) {
    val datePickerState = rememberDatePickerState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedDateText by remember { mutableStateOf("") }
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    tanggal?.let { selectedDateText = formatter.format(Date(it)) }
    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            showDialog = true
        }) {
            Text("Pilih Tanggal")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Tanggal yang dipilih: $selectedDateText")
    }
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = {
                showDialog = false
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false // Tutup dialog

                        if (datePickerState.selectedDateMillis != null) {
                            Log.d("hasil", datePickerState.selectedDateMillis.toString())
                            changeTanggal(datePickerState.selectedDateMillis)

                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentPrev(
    value: String,
    onValueChanges: (String) -> Unit,
    tanggal: Long?,
    changeTanggal: (Long?) -> Unit,
    jam: Int?,
    menit: Int?,
    changeJam: (Int?) -> Unit,
    changeMenit: (Int?) -> Unit,
    onSubmit: () -> Unit,
    enabled: Boolean,
    valueJumlah: Int,
    jumlahOnChange:(Int?)-> Unit

) {
    val iconTambah by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.add))
    val ProgresTambah by animateLottieCompositionAsState(
        composition = iconTambah,
        iterations = LottieConstants.IterateForever
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LottieAnimation(
            composition = iconTambah,
            progress = { ProgresTambah },
            modifier = Modifier.size(140.dp)
        )
        Text(
            "Tambah Praktikum",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.size(10.dp))
        HorizontalDivider()
        Column(
            modifier = Modifier.width(250.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(modifier = Modifier.size(10.dp))
            Content(value = value, onValueChanges = onValueChanges, nama = "Nama Praktikum")
            Column (
                modifier = Modifier.width(250.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Jumlah Praktikum", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = valueJumlah.toString(),
                    onValueChange = { jumlahOnChange(it.toIntOrNull()) },
                    label = { Text("Jumlah Praktikum") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            ContohDatePicker(tanggal = tanggal, changeTanggal = { changeTanggal(it) })
            Spacer(modifier = Modifier.size(10.dp))
            ContohTimePicker(
                jam = jam,
                menit = menit,
                changeJam = changeJam,
                changeMenit = changeMenit
            )
            Spacer(modifier = Modifier.size(10.dp))
        }
        Button(onClick = onSubmit, enabled = enabled) { Text("Tambah") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tambahpraktikum(
    viewModel: TambahPraktikumViewModel = viewModel(
        factory = AppViewModelFactory(
            praktikumRepo = PraktikumRepoImpl(),
            timeProvider = AndroidTimeProvider()
        )
    ),
) {
    val Failed by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cancel))
    val progresFail by animateLottieCompositionAsState(
        composition = Failed,
        iterations = LottieConstants.IterateForever
    )
    val Sukses by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cek))
    val progresSukses by animateLottieCompositionAsState(
        composition = Sukses,
        iterations = LottieConstants.IterateForever
    )
    val tanggal by viewModel.uitanggal.collectAsState()
    val nama by viewModel.uinamDocumentPrak.collectAsState()
    val jam by viewModel.uiJamTerpilih.collectAsState()
    val menit by viewModel.uiMenitTerpilih.collectAsState()
    val status by viewModel.uistatus.collectAsState()
    val jadwalSiapUpload by viewModel.jadwalTimestamp.collectAsStateWithLifecycle()
    Scaffold {
        when (status) {
            is Cek.Error -> {
                AlertDialog(
                    icon = {
                        LottieAnimation(
                            composition = Failed,
                            progress = { progresFail },
                            modifier = Modifier.size(80.dp)
                        )
                    },
                    title = {
                        Text("Gagal")
                    },
                    text = {
                        Text((status as Cek.Error).message)
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.down()
                        }) {
                            Text("Confirm")
                        }
                    },
                    onDismissRequest = {
                        viewModel.down()
                    }
                )
            }

            Cek.Loading -> Box(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Loading()
            }

            Cek.Sukses -> AlertDialog(
                icon = {
                    LottieAnimation(
                        composition = Sukses,
                        progress = { progresSukses },
                        modifier = Modifier.size(80.dp)
                    )
                },
                title = {
                    Text("Berhasil")
                },
                text = {
                    Text("Berhasil Menambahkan Praktikum")
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.down()
                    }) {
                        Text("Confirm")
                    }
                },
                onDismissRequest = {
                    viewModel.down()
                }
            )

            Cek.idle -> {
                Box(modifier = Modifier.padding(it)) {
                    ContentPrev(
                        value = nama.nama_prak,
                        onValueChanges = { viewModel.getNamaDocPrak(nama = it) },
                        tanggal = tanggal,
                        changeTanggal = { viewModel.getTanggal(tanggal = it) },
                        jam = jam,
                        menit = menit,
                        changeJam = { viewModel.getJam(jam = it) },
                        changeMenit = { viewModel.getMenit(menit = it) },
                        onSubmit = { viewModel.onSubmitHandling() },
                        enabled = jadwalSiapUpload != null,
                        valueJumlah = nama.jumlahPertemuan,
                        jumlahOnChange = { viewModel.getJumlahSlot(it)
                        Log.d("hasiljumlah","$it")
                        }
                    )
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun TambahpraktkumPrev() {
    Reglab7firebaseTheme {
        Tambahpraktikum()
    }
}