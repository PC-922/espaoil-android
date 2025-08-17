package com.pc922.espaoilandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pc922.espaoilandroid.model.AuthorizationState

@Composable
fun StatusIndicator(
    state: AuthorizationState,
    text: String,
    modifier: Modifier = Modifier
) {
    val color = when (state) {
        AuthorizationState.AUTHORIZED -> Color(0xFF2E7D32) // Verde
        AuthorizationState.DENIED -> Color(0xFFC62828) // Rojo
        AuthorizationState.NOT_DETERMINED,
        AuthorizationState.LOADING -> Color(0xFFEF6C00) // Naranja
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        ) {}
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}