package com.example.reglab7firebase.view.signup

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reglab7firebase.R
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme
import com.example.reglab7firebase.util.AndroidEmailValidator
import com.example.reglab7firebase.view.component.CusSnackBar
import com.example.reglab7firebase.view.component.CustomSnackbarVisuals
import com.example.reglab7firebase.view.component.Loading
import com.example.reglab7firebase.view.component.SecureOutline
import com.example.reglab7firebase.view.login.LoginViewModelFactory
import com.example.reglab7firebase.view.login.ViewModelLogin


@Composable
fun SignUP(viewModel: ViewModelSignUp = viewModel(
    factory = AppViewModelFactory (
        userRepo = ImplementasiUserRepo(),
        emailValidator = AndroidEmailValidator()
    )
), onBack: () -> Unit) {
    val preview by viewModel.signupstateui.collectAsState()
    val ceksignup by viewModel.stateceksignup.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(ceksignup) {
        Log.d("launchedefeknya", "cek $ceksignup")
        if (ceksignup is Cek.Error) {
            snackbarHostState.showSnackbar(
                CustomSnackbarVisuals(
                    message = (ceksignup as Cek.Error).message,
                    cek = true
                )
            )
            viewModel.onSnackDown()
        } else if (ceksignup is Cek.Sukses) {
            snackbarHostState.showSnackbar(
                CustomSnackbarVisuals(
                    message = "Berhasil Melakukan Sign Up",
                    cek = false
                )
            )
            viewModel.onSnackDown()
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                val customVisual = it.visuals as? CustomSnackbarVisuals
                if (customVisual != null) {
                    // Jika ya, gunakan Composable kustom kita
                    CusSnackBar(
                        message = customVisual.message,
                        cek = customVisual.cek
                    )
                } else {
                    // Jika tidak, gunakan Snackbar default sebagai fallback
                    Snackbar(snackbarData = it)
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFF0284C7),
                            Color(0xFFFFFFFF),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Image(
                    painterResource(R.drawable.logouad),
                    contentDescription = null,
                    modifier = Modifier.size(272.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    OutlinedTextField(
                        leadingIcon = {
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_alternate_email_24),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("Email")
                        },
                        value = preview.email,
                        onValueChange = { viewModel.Update(email = it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        leadingIcon = {
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_person_24),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("NIM")
                        },
                        value = preview.nim,
                        onValueChange = { viewModel.getNim(nim = it) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    SecureOutline(value = preview.password, onChange = {viewModel.UpdatePas(password = it)}, teks = "Password")
                    SecureOutline(value = preview.repassword, onChange = {viewModel.UpdaterePas(repassword = it)}, teks = "Re Password")
                    Button(onClick = {
                        viewModel.onClickHandling()
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text("Daftar")
                    }

                }
                Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text("Sudah Punya Akun?")
                    TextButton(onClick = {onBack()}) { Text("Login", fontWeight = FontWeight.Bold) }
                }

            }
            when (ceksignup) {
                is Cek.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray)
                            .alpha(0.8f),
                        contentAlignment = Alignment.Center
                    ) {
                        Card (modifier = Modifier.padding(20.dp), elevation = CardDefaults.cardElevation(4.dp)){
                            Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center){
                                Loading()
                            }
                        }

                    }
                }
                Cek.Sukses -> Log.d("berhasil", "")
                Cek.idle -> Log.d("idle", "")
                is Cek.Error -> Log.d("error", "errornya ${(ceksignup as Cek.Error).message}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignUpPrev() {
    Reglab7firebaseTheme {

    }

}