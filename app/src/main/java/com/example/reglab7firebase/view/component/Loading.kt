package com.example.reglab7firebase.view.component

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.reglab7firebase.R

@Composable
fun Loading(modifier: Modifier = Modifier) {
    val iconLoading by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.laoding))
    val progresLoading by animateLottieCompositionAsState(
        composition = iconLoading,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        composition = iconLoading,
        progress = {progresLoading},
        modifier = Modifier.size(80.dp)
    )
}