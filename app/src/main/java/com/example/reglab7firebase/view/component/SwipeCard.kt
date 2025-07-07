package com.example.reglab7firebase.view.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme


@Composable
fun Visibleee(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
// Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
// Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        Text(
            "Hello",
            Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            visible = visible != true
        }
    ) { }
}

@Preview(showBackground = true)
@Composable
private fun VisPrev() {
    Reglab7firebaseTheme {
        Visibleee()
    }

}
