package com.pc922.espaoilandroid.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun DistanceTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val regex = remember { Regex("^[0-9]*\\.?[0-9]{0,2}$") } // admite decimales
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            if (input.isEmpty() || regex.matches(input)) onValueChange(input)
        },
        modifier = modifier,
        singleLine = true,
        label = { Text("Radio de búsqueda (km):") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        )
    )
}