package com.example.reglab7firebase.view.dashboardAdmin.titikMap

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.repository.LocationRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.view.component.DialogAlert
import com.example.reglab7firebase.view.component.Loading
import com.example.reglab7firebase.view.presensi.hasPermission
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun Permessions(modifier: Modifier = Modifier, onRequestPermission: () -> Unit) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val isPermanentlyDenied =
        !ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) ||
                !ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (isPermanentlyDenied) {
            DialogAlert(
                isi = "Aplikasi ini membutuhkan izin Kamera dan Lokasi untuk berfungsi.",
                keadaan = false,
                confirmButon = {
                    TextButton(onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", context.packageName, null)
                        intent.data = uri
                        context.startActivity(intent)
                    }) { Text("Baiklah") }
                },
                dismisReq = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                }
            )
        } else {
            DialogAlert(
                isi = "Minta Izin Lokasi dan Kamera Dulu!",
                keadaan = false,
                confirmButon = {
                    TextButton(onClick = {
                        onRequestPermission()
                    }) { Text("OK") }
                },
                dismisReq = {
                    onRequestPermission()
                },
            )
        }

    }
}

@Composable
fun MapScreen(modifier: Modifier, onClick: (LatLng) -> Unit) {
    // 1. State untuk menyimpan lokasi (koordinat) yang dipilih pengguna.
    // Inisialisasi dengan null karena belum ada lokasi yang dipilih.
    var selectedLocation by remember {
        mutableStateOf<LatLng?>(null)
    }

    // Lokasi awal kamera, misalnya Jakarta
    val jakarta = LatLng(-7.823601713107916, 110.3763627765288)
    val cameraPositionState = rememberCameraPositionState() {
        position = CameraPosition.fromLatLngZoom(jakarta, 10f)
    }

    Box(modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            // 2. onMapClick adalah event listener yang dipanggil saat peta diketuk.
            // 'it' berisi objek LatLng dari lokasi yang diketuk.
            onMapClick = {
                onClick(it)
                selectedLocation = it
            }
        ) {
            // 3. Tampilkan Marker jika lokasi sudah dipilih (tidak null).
            selectedLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Lokasi Dipilih",
                    snippet = "Lat: ${it.latitude}, Lng: ${it.longitude}"
                )
            }
        }

        // 4. Tampilkan teks koordinat di bagian bawah layar.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = if (selectedLocation != null) {
                    "Lat: ${selectedLocation!!.latitude}\nLng: ${selectedLocation!!.longitude}"
                } else {
                    "Ketuk peta untuk memilih lokasi"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PilihMap(
    modifier: Modifier = Modifier,
    viewModel: PilihMapViewModel = viewModel(
        factory = AppViewModelFactory(
            // Buat implementasi nyata di sini, karena Composable punya akses ke Context
            locationRepo = LocationRepoImpl(LocalContext.current)
        )
    ),
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    viewModel.getPermessionFineLocation(context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
    viewModel.getPermessionCoarseLocation(context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
    val izinfine by viewModel.uifinelocation.collectAsStateWithLifecycle()
    val izinCoarse by viewModel.uiCoarseLocation.collectAsStateWithLifecycle()
    val lokasi by viewModel.uiLokasi.collectAsStateWithLifecycle()
    val status by viewModel.uistatus.collectAsState()
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getPermessionFineLocation(context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
                viewModel.getPermessionCoarseLocation(context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            viewModel.getPermessionFineLocation(it[Manifest.permission.ACCESS_FINE_LOCATION] == true)
            viewModel.getPermessionCoarseLocation(it[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
        })
    val permessionMinta =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    if (izinfine == false || izinCoarse == false) {
        Permessions(onRequestPermission = { permissionLauncher.launch(permessionMinta) })
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(10.dp), contentAlignment = Alignment.Center
        ) {
            when (status) {
                is Cek.Error -> {
                    DialogAlert(
                        isi = (status as Cek.Error).message,
                        keadaan = true,
                        confirmButon = { Button(onClick = { viewModel.onIdle() }) { Text("Ok") } },
                        dismisReq = {}
                    )
                }

                Cek.Loading -> Loading()
                Cek.Sukses -> {
                    DialogAlert(
                        isi = "Berhasil mengganti lokasi",
                        keadaan = true,
                        confirmButon = { Button(onClick = { viewModel.onIdle() }) { Text("Ok") } },
                        dismisReq = {}
                    )
                }

                Cek.idle -> {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        MapScreen(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.7f),
                            onClick = { viewModel.setLocation(it) }
                        )
                        Button(
                            onClick = { viewModel.handlingClikLocation() },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Set Lokasi") }
                    }
                }
            }
        }
    }
}