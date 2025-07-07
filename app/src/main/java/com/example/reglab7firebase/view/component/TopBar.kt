package com.example.reglab7firebase.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.reglab7firebase.R
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme

@Composable
fun Elemenair(modifier: Modifier = Modifier,arah: Boolean) {

    val composition by rememberLottieComposition(
        spec = if (arah) LottieCompositionSpec.RawRes(R.raw.kiri) else LottieCompositionSpec.RawRes(R.raw.kanan)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        restartOnPlay = false
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

@Composable
fun TopBar(nama: String, nim: String, onClickMenu: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Card(elevation = CardDefaults.cardElevation(5.dp)) {
            Box(modifier = Modifier
                .wrapContentHeight()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFFDF59),
                            Color(0xFFFFFFFF),
                        )
                    )
                ), contentAlignment = Alignment.Center){

                Elemenair(modifier = Modifier.size(50.dp).align(Alignment.BottomStart),arah = true)
                Elemenair(modifier = Modifier.size(50.dp).align(Alignment.TopEnd),arah = false)

                Row(
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.95f)
                        .padding(5.dp),
                    Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(0.7f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_person_24),
                            contentDescription = "person icon",
                            modifier = Modifier.size(35.dp)
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {
                            Text(nama, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(nim, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }

                    }
                    IconButton(
                        onClick = { onClickMenu() },
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.baseline_menu_24),
                            contentDescription = "menu icon",
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }

            }
        }

    }

}

@Preview(showBackground = true)
@Composable
private fun TopBarPrev() {
    Reglab7firebaseTheme {
        TopBar(nama = "Yasir Khoirul Hudaas dasdsadasdasdasdasdasd", nim = "2100018132", onClickMenu = {})
    }
}