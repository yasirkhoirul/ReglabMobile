package com.example.reglab7firebase.view.presensi

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.reglab7firebase.R
import com.example.reglab7firebase.data.model.Cek
import com.example.reglab7firebase.data.model.LocationResult
import com.example.reglab7firebase.data.model.TimeRepo
import com.example.reglab7firebase.data.repository.ImplementasiUserRepo
import com.example.reglab7firebase.data.repository.LocationRepoImpl
import com.example.reglab7firebase.data.repository.PraktikumRepoImpl
import com.example.reglab7firebase.data.repository.TimeRepoImpl
import com.example.reglab7firebase.data.repository.WifiRepoImpl
import com.example.reglab7firebase.factory.AppViewModelFactory
import com.example.reglab7firebase.util.QrCodeAnalyzer
import com.example.reglab7firebase.view.component.DialogAlert
import com.example.reglab7firebase.view.component.DialogAlertCancel
import com.example.reglab7firebase.view.component.Loading
import com.example.reglab7firebase.view.component.Location
import java.util.concurrent.Executors
import kotlin.math.roundToInt

@Composable
fun Camera(modifier: Modifier, onQrCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val executor = Executors.newSingleThreadExecutor()
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val previewUseCase = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysisUseCase = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(executor, QrCodeAnalyzer { result ->
                            onQrCodeScanned(result)
                        })
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        previewUseCase,
                        imageAnalysisUseCase
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        }
    )
}

@Composable
fun Isi(modifier: Modifier, lokasi: LocationResult, wifi: String?, viewModel: PresensiViewModel) {
    val waktu by viewModel.uiwaktu.collectAsStateWithLifecycle()
    val status by viewModel.uistatus.collectAsStateWithLifecycle()
    var scannedResult by remember { mutableStateOf<String?>(null) }
    val result by viewModel.uiresult.collectAsStateWithLifecycle()
    Log.d("scresultnyascan", scannedResult.toString())
    Card(elevation = CardDefaults.elevatedCardElevation(10.dp), modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .background(Color.White)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (wifi != null) {
                    Column(
                        modifier = Modifier.weight(0.5f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.outline_wifi_24),
                            contentDescription = null
                        )
                        Text(wifi, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                } else {
                    Column(
                        modifier = Modifier.weight(0.5f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_wifi_off_24),
                            contentDescription = null
                        )
                        Text("Wifi Tidak Ditemukan", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                when (lokasi) {
                    is LocationResult.Error -> {
                        Column(
                            modifier = Modifier.weight(0.5f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_location_off_24),
                                contentDescription = null
                            )
                            Text(lokasi.message, modifier = Modifier)
                        }
                    }

                    LocationResult.Loading -> {
                        Column(
                            modifier = Modifier.weight(0.5f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.outline_edit_location_alt_24),
                                contentDescription = null
                            )
                            Text("Loading...")
                        }
                    }

                    LocationResult.LocationUnavailable -> {
                        Column(
                            modifier = Modifier.weight(0.5f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_location_off_24),
                                contentDescription = null
                            )
                            Text("Silahkan Hidupkan GPS")
                        }

                    }

                    is LocationResult.Success -> {
                        Column(
                            modifier = Modifier.weight(0.5f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.outline_location_on_24),
                                contentDescription = null
                            )
                            Text(
                                "Jarak dari kampus : ${lokasi.distanceToCampus.roundToInt()} meter",
                                modifier = Modifier,
                                textAlign = TextAlign.Center
                            )
                        }

                    }
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            if (lokasi is LocationResult.Success && !wifi.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                        .align(Alignment.CenterHorizontally), contentAlignment = Alignment.Center
                ) {
                    if (lokasi.distanceToCampus.roundToInt() <= 50 && wifi == "koscoklat") {
                        if (status is Cek.idle) {
                            Camera(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .fillMaxSize(),
                                onQrCodeScanned = { result ->
                                    Log.d("hasilresulit", result)
                                    if (result.isNotEmpty()) {
                                        viewModel.validasiPresensi(result)
                                    }
                                }
                            )
                        }
                        if (status is Cek.Sukses) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.7f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "Berhasil Melakukan Presensi",
                                        color = Color.White,
                                        fontSize = 24.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(onClick = {
                                        scannedResult = null
                                        viewModel.getIle()
                                    }) { // Tombol untuk scan lagi
                                        Text("Scan Lagi")
                                    }
                                }
                            }
                        }
                        if (status is Cek.Error) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.7f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("MAAF!", color = Color.White, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text((status as Cek.Error).message)
                                    Button(onClick = {
                                        scannedResult = null
                                        viewModel.getIle()
                                    }) { // Tombol untuk scan lagi
                                        Text("Scan Lagi")
                                    }
                                }
                            }
                        }
                        if (status is Cek.Loading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.7f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Loading()
                                }
                            }
                        }
                    } else {
                        Text(
                            "Anda harus didekat kampus dan menyambung wifi UAD, minimal 50 meter",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Location(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.size(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Scan QR COde")
            }
        }
    }
}

@Composable
fun Permession(modifier: Modifier = Modifier, onRequestPermission: () -> Unit) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity
    val isPermanentlyDenied = !ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.CAMERA
    ) ||
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

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun Presensi(
    modifier: Modifier,
    viewModel: PresensiViewModel = viewModel(
        factory = AppViewModelFactory(
            timeRepo = TimeRepoImpl(),
            userRepo = ImplementasiUserRepo(),
            praktikumRepo = PraktikumRepoImpl(),
            locationRepo = LocationRepoImpl(LocalContext.current),
            wifiRepo = WifiRepoImpl(LocalContext.current)
        )
    ),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    viewModel.getPermessionFineLocation(context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
    viewModel.getPermessionCam(context.hasPermission(Manifest.permission.CAMERA))
    viewModel.getPermessionCoarseLocation(context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION))
    val lokasi by viewModel.uiLokasi.collectAsStateWithLifecycle()
    val status by viewModel.uistatus.collectAsStateWithLifecycle()
    val wifi by viewModel.uiwifi.collectAsStateWithLifecycle()
    val izinkamera by viewModel.uiCameraPermession.collectAsStateWithLifecycle()
    val izinfine by viewModel.uifinelocation.collectAsStateWithLifecycle()
    val izinCoarse by viewModel.uiCoarseLocation.collectAsStateWithLifecycle()
    LaunchedEffect(lokasi) {
        if (wifi != null || lokasi is LocationResult.Success) {
            viewModel.observeWifiChanges()
        }
    }

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getPermessionFineLocation(context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
                viewModel.getPermessionCam(context.hasPermission(Manifest.permission.CAMERA))
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
            viewModel.getPermessionCam(it[Manifest.permission.CAMERA] == true)
            viewModel.getPermessionCoarseLocation(it[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
        })
    val permessionMinta =
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    if (status is Cek.Loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Loading()
        }
    } else if (status is Cek.Error) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text((status as Cek.Error).message)
        }
    } else {
        if (izinfine == false || izinkamera == false || izinCoarse == false) {
            Permession(onRequestPermission = { permissionLauncher.launch(permessionMinta) })
        } else {
            LaunchedEffect(Unit) {
                viewModel.observeLocation()
                viewModel.observeWifiChanges()
            }
            Box(modifier = modifier) {
                Isi(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(20.dp), lokasi = lokasi, wifi = wifi, viewModel = viewModel
                )
            }
        }
    }


}

@Preview
@Composable
private fun PresensiPrev() {
    Isi(modifier = Modifier, lokasi = LocationResult.Loading, wifi = "", viewModel())
}