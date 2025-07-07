package com.example.reglab7firebase.view.login


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reglab7firebase.R
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme
import com.example.reglab7firebase.util.AndroidEmailValidator
import com.example.reglab7firebase.view.component.DialogAlert
import com.example.reglab7firebase.view.component.Loading
import com.example.reglab7firebase.view.component.SecureOutline

@Composable
fun Login(
    viewModelLogin: ViewModelLogin = viewModel(
        factory = LoginViewModelFactory(
            ImplementasiUserRepo(),
            AndroidEmailValidator()
        )
    ),
    onNavigatetoSignup: () -> Unit,
    onNavigateDash: () -> Unit,
    onNavigateAdmin:()-> Unit
) {
    val status by viewModelLogin.uistatus.collectAsState()
    val iadmin by viewModelLogin.uiIsAdmin.collectAsState()
    LaunchedEffect(status) {
        if (status is Cek.Sukses && iadmin == false) {
            onNavigateDash()
        }
        if (status is Cek.Sukses && iadmin == true){
            onNavigateAdmin()
        }
    }
    val previewValue by viewModelLogin.uiState.collectAsState()
    Scaffold {
        Box(
            modifier = Modifier
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
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                if (status is Cek.Loading){
                    Loading()
                }else if (status is Cek.Sukses) {
                    Box(modifier = Modifier.fillMaxSize())
                }else if (status is Cek.Error){
                    DialogAlert(
                        isi = (status as Cek.Error).message, keadaan = false, confirmButon = {
                            TextButton(onClick = {
                                viewModelLogin.Down()
                            }) { Text("OK") }
                        },
                        dismisReq = {viewModelLogin.Down()}
                    )
                }
                else{
                    Image(
                        painterResource(R.drawable.logouad),
                        contentDescription = "Logo Uad",
                        modifier = Modifier.size(272.dp)
                    )
                    Text("Selamat Datang di Reglab UAD")
                    Text("Login", fontWeight = FontWeight.Bold)
                    Column(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            onValueChange = { viewModelLogin.UpdateName(nama = it) },
                            value = previewValue.email,
                            label = {
                                Text("Email")
                            },
                            leadingIcon = {
                                Image(
                                    imageVector = ImageVector.vectorResource(R.drawable.baseline_alternate_email_24),
                                    contentDescription = null
                                )
                            })
                        SecureOutline(
                            value = previewValue.password,
                            onChange = { viewModelLogin.UpdatePassword(password = it) },
                            teks = "Password"
                        )

                        Button(
                            onClick = { viewModelLogin.onclickHandling() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("MASUK")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Belum Punya Akun?")
                            TextButton(onClick = { onNavigatetoSignup() }) {
                                Text(
                                    "Daftar",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun LoginPrev() {
    Reglab7firebaseTheme {
        Login(
            onNavigatetoSignup = { },
            onNavigateDash = {},
            onNavigateAdmin = {}
        )
    }
}