package com.example.reglab7firebase.view.component

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.reglab7firebase.R
import com.example.reglab7firebase.data.model.Cek

@Composable
fun DialogAlert(
    isi: String,
    keadaan: Boolean,
    confirmButon: @Composable () -> Unit,
    dismisReq: () -> Unit,
) {
    val res = if (keadaan == true) {
        LottieCompositionSpec.RawRes(R.raw.cek)
    } else {
        LottieCompositionSpec.RawRes(R.raw.cancel)
    }
    val Failed by rememberLottieComposition(res)
    val progresFail by animateLottieCompositionAsState(
        composition = Failed,
        iterations = LottieConstants.IterateForever
    )
    AlertDialog(
        icon = {
            LottieAnimation(
                composition = Failed,
                progress = { progresFail },
                modifier = Modifier.size(80.dp)
            )
        },
        title = {
            Text(text = if (keadaan) "berhasil" else "Gagal")
        },
        text = {
            Text(isi)
        },
        confirmButton = confirmButon,
        onDismissRequest = dismisReq
    )
}

@Composable
fun DialogAlertCancel(
    judul: String,
    isi: String,
    keadaan: Boolean,
    confirmButon: @Composable () -> Unit,
    dismisReq: () -> Unit,
    dismisBbut: @Composable () -> Unit,
) {
    val res = if (keadaan == true) {
        LottieCompositionSpec.RawRes(R.raw.cek)
    } else {
        LottieCompositionSpec.RawRes(R.raw.cancel)
    }
    val Failed by rememberLottieComposition(res)
    val progresFail by animateLottieCompositionAsState(
        composition = Failed,
        iterations = LottieConstants.IterateForever
    )
    AlertDialog(
        icon = {
            LottieAnimation(
                composition = Failed,
                progress = { progresFail },
                modifier = Modifier.size(80.dp)
            )
        },
        title = {
            Text(judul)
        },
        text = {
            Text(isi)
        },
        confirmButton = confirmButon,
        onDismissRequest = dismisReq,
        dismissButton = dismisBbut
    )
}

@Composable
fun DialogForm(
    judul: String,
    isi: String,
    confirmButon: @Composable () -> Unit,
    dismisReq: () -> Unit,
    dismisBbut: @Composable () -> Unit,
    onValueChange: (String) -> Unit,
    checked: Boolean,
    onchangeCHeck:(Boolean)-> Unit

) {
    val tambah by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.add))
    val progresFail by animateLottieCompositionAsState(
        composition = tambah,
        iterations = LottieConstants.IterateForever
    )
    AlertDialog(
        icon = {
            LottieAnimation(
                composition = tambah,
                progress = { progresFail },
                modifier = Modifier.size(80.dp)
            )
        },
        title = {
            Text(judul)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = isi,
                    onValueChange = { onValueChange(it) },
                    label = { Text("nilai") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "kehadiran"
                    )
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { onchangeCHeck(it)}
                    )
                }
            }
        },
        confirmButton = confirmButon,
        onDismissRequest = dismisReq,
        dismissButton = dismisBbut
    )
}