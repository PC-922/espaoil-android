package com.pc922.espaoilandroid.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pc922.espaoilandroid.model.SortOption

@Composable
fun SortSegmentedControl(
    selected: SortOption,
    onSelected: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SegmentButton(
                text = "Precio",
                selected = selected == SortOption.PRICE,
                onClick = { onSelected(SortOption.PRICE) }
            )
            SegmentButton(
                text = "Distancia",
                selected = selected == SortOption.DISTANCE,
                onClick = { onSelected(SortOption.DISTANCE) }
            )
        }
    }
}

@Composable
private fun SegmentButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    val colors = if (selected) {
        ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    } else {
        ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }
    OutlinedButton(
        onClick = onClick,
        shape = shape,
        modifier = Modifier.height(36.dp),
        border = null,
        colors = colors,
        content = { Text(text) }
    )
}