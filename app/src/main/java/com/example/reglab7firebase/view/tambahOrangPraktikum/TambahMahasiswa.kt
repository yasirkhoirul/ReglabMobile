package com.example.reglab7firebase.view.tambahOrangPraktikum

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.User
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.data.repository.PraktikumRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.view.component.DialogAlert
import com.example.reglab7firebase.view.component.Loading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Container(
    mahasiswadipilih: List<String>,
    hasilPencarian: List<User>,
    search: String,
    idPrak: String,
    onValueChangeSearch:(String)-> Unit,
    onClickDropDown:(String)-> Unit,
    onClickHapusPeo:(String)->Unit,
    onClickSubmit:(String, List<String>)-> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    Scaffold {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                TextField(
                    value = search,
                    onValueChange = {
                        Log.d("hasilPencarian", it)
                        onValueChangeSearch(it)
                        isDropdownExpanded = true
                    },
                    label = { Text("Cari Nama atau UID Mahasiswa") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor() //
                )

                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    hasilPencarian.forEach { mahasiswa ->
                        DropdownMenuItem(
                            text = { Text(mahasiswa.nim) },
                            onClick = {
                                onClickDropDown(mahasiswa.uid)
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Mahasiswa Terpilih:", style = MaterialTheme.typography.titleMedium)

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(mahasiswadipilih) { mahasiswa ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(mahasiswa)
                        IconButton(onClick = {
                            onClickHapusPeo(mahasiswa)

                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Hapus"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onClickSubmit(idPrak,mahasiswadipilih)

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Daftar Mahasiswa")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahMahasiswaScreen(
    viewModel: TambahMahasiswaViewModel = viewModel(
        factory = AppViewModelFactory(
            userRepo = ImplementasiUserRepo(),
            praktikumRepo = PraktikumRepoImpl()
        )
    ),
    idPrak: String,
    isAsprak: Boolean
) {
    Log.d("isasprak",isAsprak.toString())
    var dismis by rememberSaveable { mutableStateOf(false) }
    val search by viewModel.uisearch.collectAsStateWithLifecycle()
    val hasilPencarian by viewModel.uiHasilPencarian.collectAsStateWithLifecycle()
    val mahasiswadipilih by viewModel.uihasildipilih.collectAsStateWithLifecycle()
    val status by viewModel.uistatus.collectAsStateWithLifecycle()


    if (status is Cek.Error) {
        DialogAlert(
            isi = (status as Cek.Error).message,
            keadaan = false,
            confirmButon = {
                TextButton(onClick = {
                    dismis = !dismis
                    viewModel.down()
                }) { Text("OK") }
            },
            dismisReq = {
                dismis = !dismis
                viewModel.down()
            })
    } else if (status is Cek.Sukses) {
        DialogAlert(
            isi = "Berhasil Menambahkan",
            keadaan = true,
            confirmButon = {
                TextButton(onClick = {
                    dismis = !dismis
                    viewModel.down()
                }) { Text("OK") }
            },
            dismisReq = {
                dismis = !dismis
                viewModel.down()
            })
    }else if (status is Cek.Loading){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            Loading()
        }

    }else{
        Container(
            onValueChangeSearch = { viewModel.onSearchQueryChanged(it) },
            mahasiswadipilih = mahasiswadipilih,
            hasilPencarian = hasilPencarian,
            search = search,
            idPrak = idPrak,
            onClickDropDown = { viewModel.tambahmahasiswadipilih(it)
                viewModel.onSearchQueryChanged("") },
            onClickHapusPeo = {
                viewModel.hapusmahasiswadipilih(it)
            },
            onClickSubmit = { idPrak, mahasiswadipilih->
                if (isAsprak){
                    viewModel.tambahAsprak(idpraks = idPrak, lists = mahasiswadipilih)
                }else{
                    viewModel.tambahMahasiswa(idpraks = idPrak, lists = mahasiswadipilih)
                }
            }
        )
    }

}


@Preview(showBackground = true)
@Composable
private fun PrakPrev() {
    TambahMahasiswaScreen(
        idPrak = "asdasdasd",
        isAsprak = false,
    )
}