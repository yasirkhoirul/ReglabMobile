package com.example.reglab7firebase.view.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.reglab7firebase.R
import com.example.reglab7firebase.data.model.Dummy
import com.example.reglab7firebase.data.model.Isi
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme
import com.example.reglab7firebase.ui.theme.abuabu
import kotlinx.coroutines.launch

@Composable
fun DrawableNavigationAdmin(
    modifier: Modifier = Modifier,
    signOut: () -> Unit,
    nama: String,
    nim: String,
    isiinformasi: List<Isi>,
    toTambahPrak: () -> Unit,
    klikIsi: (String) -> Unit,
    klikPrak: () -> Unit,
    klikKoor: () -> Unit,
    kliktitik: () -> Unit,
    tambahInfor: () -> Unit,
    deleteInfor: (String) -> Unit,
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.logouad),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            "Reglab Informatika",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    HorizontalDivider()
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                    ) {
                        TextButton(
                            onClick = { toTambahPrak() },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Praktikum Admin", color = Color.Black) }
                    }
                    HorizontalDivider()
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                    ) {
                        TextButton(
                            onClick = signOut,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Keluar Akun") }
                    }
                }

            }
        }
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopBar(nama = nama, nim = nim, onClickMenu = {
                    scope.launch {
                        drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                })
            },
        )


        {
            IsiDashAdmin(
                isiinformasi = isiinformasi,
                klikIsi = klikIsi,
                modifier = modifier
                    .padding(it)
                    .fillMaxSize(),
                klikprak = klikPrak,
                klikKoor = klikKoor,
                kliktitik = kliktitik,
                tambahInfor = tambahInfor,
                deleteInfor = deleteInfor
            )
        }
    }
}


@Composable
fun IsiDashAdmin(
    isiinformasi: List<Isi>,
    klikIsi: (String) -> Unit,
    klikKoor: () -> Unit,
    klikprak: () -> Unit,
    kliktitik: () -> Unit,
    tambahInfor: () -> Unit,
    modifier: Modifier,
    deleteInfor: (String) -> Unit,
) {
    Box(
        modifier = modifier

    ) {
        val asistencom by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.finlokasi))
        val progressassiten by animateLottieCompositionAsState(
            composition = asistencom,
            iterations = LottieConstants.IterateForever
        )
        val compositionkoorT by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.coor))

        val progresskoorT by animateLottieCompositionAsState(
            composition = compositionkoorT,
            iterations = LottieConstants.IterateForever
        )
        val compositionkoor by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.coor))

        val progresskoor by animateLottieCompositionAsState(
            composition = compositionkoor,
            iterations = LottieConstants.IterateForever
        )
        var expanded by remember { mutableStateOf(false) }
        val heightFraction by animateFloatAsState(
            targetValue = if (expanded) 1f else 0.8f,
            label = "heightAnimation"
        )
        val density = LocalDensity.current
        AnimatedVisibility(
            visible = !expanded,
            enter = slideInVertically {
                // Slide in from 40 dp from the top.
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.2f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    //kiri
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .weight(0.33f)
                            .padding(10.dp)
                    ) {

                        Card(
                            onClick = { kliktitik() },
                            elevation = CardDefaults.cardElevation(5.dp),
                            modifier = Modifier
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .background(
                                        brush = Brush.linearGradient(
                                            listOf(
                                                Color(0xFFA2CCF5),
                                                Color.White
                                            )
                                        )
                                    )
                                    .padding(10.dp)
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                LottieAnimation(
                                    composition = asistencom,
                                    progress = { progressassiten },
                                    modifier = Modifier.size(50.dp)
                                )
                                Text("Set Titik", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .weight(0.33f)
                            .padding(10.dp)
                    ) {

                        Card(
                            onClick = { klikprak() },
                            elevation = CardDefaults.cardElevation(5.dp),
                            modifier = Modifier
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .background(
                                        brush = Brush.linearGradient(
                                            listOf(
                                                Color(0xFFA2CCF5),
                                                Color.White
                                            )
                                        )
                                    )
                                    .padding(10.dp)
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                LottieAnimation(
                                    composition = compositionkoorT,
                                    progress = { progresskoorT },
                                    modifier = Modifier.size(50.dp)
                                )
                                Text("Praktikum", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }


        //bawah
        Column(
            modifier = Modifier
                .animateContentSize()
                .align(Alignment.BottomEnd)
                .fillMaxHeight(heightFraction)
                .fillMaxWidth()
                .padding(15.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    expanded = !expanded
                }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize(),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFFFFFFFF),
                                    Color(0xFF0284C7)
                                )
                            )
                        )
                ) {
                    //background info
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = (-120).dp, y = (-150).dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(
                                        Color(0xFFFFFFFF),
                                        Color(0xFFFFFFFF),
                                        Color(0xFF0284C7)
                                    )
                                )
                            )
                            .size(350.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = (-150).dp, y = (-150).dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(
                                        Color(0xFFFFFFFF),
                                        Color(0xFFFFFFFF),
                                        Color(0xFF0284C7)
                                    )
                                )
                            )
                            .size(300.dp)
                    )
                    //background info
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(15.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Informasi",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(0.3f)
                            )
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.baseline_keyboard_double_arrow_up_24),
                                contentDescription = null,
                                modifier = Modifier
                                    .weight(0.3f)
                                    .rotate(degrees = if (expanded) 180f else 0f)
                            )
                            IconButton(onClick = { tambahInfor() }) {
                                Image(
                                    imageVector = ImageVector.vectorResource(R.drawable.baseline_add_circle_outline_24),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(IntrinsicSize.Max)
                                        .weight(0.3f)
                                        .align(Alignment.Bottom)
                                )
                            }

                        }


                        LazyColumn(
                            contentPadding = PaddingValues(start = 6.dp, top = 12.dp)
                        ) {
                            items(isiinformasi) {
                                Card(
                                    modifier = Modifier.clickable(
                                        onClick = {
                                            klikIsi(it.uid)
                                        }
                                    ),
                                    elevation = CardDefaults.cardElevation(5.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(5.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                it.judul,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(onClick = { deleteInfor(it.uid) }) {
                                                Image(
                                                    imageVector = ImageVector.vectorResource(R.drawable.outline_cancel_24),
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(5.dp))
                                        Column(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(5.dp))
                                                .border(
                                                    width = 1.dp,
                                                    color = abuabu,
                                                    shape = RoundedCornerShape(5.dp)
                                                )
                                                .fillMaxWidth()
                                                .padding(5.dp)
                                                .heightIn(min = 50.dp, max = 150.dp)

                                        ) {
                                            Text(it.isi)
                                        }

                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                    }

                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun DrawablePreviews() {
    Reglab7firebaseTheme {
        DrawableNavigationAdmin(
            signOut = {},
            nama = "yasir",
            nim = "Admin",
            isiinformasi = Dummy().isinyas,
            klikIsi = {},
            klikPrak = {},
            toTambahPrak = {},
            modifier = Modifier,
            klikKoor = { },
            kliktitik = { },
            tambahInfor = {},
            deleteInfor = {}
        )
    }
}