package com.example.reglab7firebase.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.reglab7firebase.R
import com.example.reglab7firebase.ui.theme.Reglab7firebaseTheme

@Composable
fun SecureOutline(modifier: Modifier = Modifier,value: String,onChange:(String) -> Unit,teks: String) {
    var passwordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        maxLines = 1,
        visualTransformation =
            if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Default.Visibility
            else Icons.Default.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = image,
                    contentDescription = "Toggle Password Visibility"
                )
            }
        },
        leadingIcon = {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.outline_lock_24),
                contentDescription = null
            )
        },
        label = {
            Text(teks)
        },
        value = value,
        onValueChange = onChange
    )

}

@Preview
@Composable
private fun SecureOutlinePrev() {
    Reglab7firebaseTheme {
        SecureOutline(value = "", onChange = {}, teks = "")
    }

}