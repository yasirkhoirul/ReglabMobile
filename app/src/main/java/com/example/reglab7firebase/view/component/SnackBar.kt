package com.example.reglab7firebase.view.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
private fun animateFadeAndScaleSnackBar(): Pair<Float, Float> {
    val alpha by animateFloatAsState(
        targetValue = 1f, // Fade to full opacity
        animationSpec = tween(
            durationMillis = SnackBarConstants.ALPHA_DURATION, // Fade duration
            easing = FastOutSlowInEasing // Smooth easing
        ),
        label = "alpha"
    )
    val scale by animateFloatAsState(
        targetValue = 1f, // Scale to full size
        animationSpec = tween(
            durationMillis = SnackBarConstants.SCALE_DURATION, // Scale duration
            easing = FastOutSlowInEasing // Smooth easing
        ),
        label = "scale"
    )
    return alpha to scale // Return animation pair
}
private object SnackBarConstants {
    val PADDING = 8.dp // Outer padding
    val CONTENT_PADDING = 12.dp // Inner padding
    val CORNER_RADIUS = 12.dp // Corner radius
    val ICON_SIZE = 24.dp // Icon size
    val ICON_PADDING = 8.dp // Icon padding
    val SPACING = 8.dp // Spacing between elements
    val BUTTON_HEIGHT = 36.dp // Button height
    val MESSAGE_FONT_SIZE = 14.sp // Message text size
    val ACTION_FONT_SIZE = 14.sp // Action text size
    const val ALPHA_DURATION = 250 // Fade duration (ms)
    const val SCALE_DURATION = 200 // Scale duration (ms)
}




@Composable
fun CusSnackBar(modifier: Modifier = Modifier, message: String , cek: Boolean) {
    val (alphanim,scalenim) = animateFadeAndScaleSnackBar()
    val color1 = if (cek){
        listOf(Color(0xFFE91E63), Color(0xFFDC9CB1))
    }else{
        listOf(Color(0xFF4CAF50), Color(0xFF81C784))
    }
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .alpha(alphanim)
            .scale(scalenim)
            .padding(SnackBarConstants.PADDING),
        shape = RoundedCornerShape(SnackBarConstants.CORNER_RADIUS), // Rounded corners
        colors = CardDefaults.cardColors(containerColor = Color.Transparent), // Transparent for gradient
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) // Shadow
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = color1 // Green gradient
                    )
                )
                .padding(SnackBarConstants.CONTENT_PADDING) // Inner padding
                .height(IntrinsicSize.Min), // Minimum height based on content
            verticalAlignment = Alignment.CenterVertically, // Center vertically
            horizontalArrangement = Arrangement.SpaceBetween // Space items
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f) // Take available space
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = "Info",
                    tint = Color.White,
                    modifier = Modifier
                        .size(SnackBarConstants.ICON_SIZE) // Icon size
                        .padding(end = SnackBarConstants.ICON_PADDING) // Icon padding
                )
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = SnackBarConstants.MESSAGE_FONT_SIZE, // Message font size
                    textAlign = TextAlign.Start,
                    lineHeight = SnackBarConstants.MESSAGE_FONT_SIZE * 1.2f, // Line height
                    modifier = Modifier.padding(end = SnackBarConstants.SPACING) // Spacing
                )
            }

        }

    }
}
data class CustomSnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val withDismissAction: Boolean = false,
    val cek: Boolean,
    // Properti kustom kita: sebuah ikon!
    val icon: ImageVector? = null
) : SnackbarVisuals


@Preview(showBackground = true)
@Composable
private fun SnackBarPrev() {
    Reglab7firebaseTheme {
      CusSnackBar(message = "Harus mengisi semua kolom", cek = true)
    }
}