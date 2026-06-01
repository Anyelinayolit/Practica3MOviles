package com.compensatuviaje.tracker.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BigActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    destructive: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = if (destructive) ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
        ) else ButtonDefaults.buttonColors(),
        modifier = modifier.fillMaxWidth().height(56.dp),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun GpsStatusIcon(hasSignal: Boolean) {
    if (hasSignal) {
        Icon(Icons.Default.GpsFixed, contentDescription = "GPS activo", tint = Color(0xFF4CAF50))
    } else {
        Icon(Icons.Default.GpsOff, contentDescription = "Sin GPS", tint = Color(0xFFF44336))
    }
}

@Composable
fun SyncStatusIcon(isOnline: Boolean, recentlySynced: Boolean) {
    val tint = when {
        isOnline && recentlySynced -> Color(0xFF4CAF50)
        isOnline -> Color(0xFFFFA000)
        else -> Color(0xFF9E9E9E)
    }
    if (isOnline) {
        Icon(Icons.Default.Cloud, contentDescription = "En línea", tint = tint)
    } else {
        Icon(Icons.Default.CloudOff, contentDescription = "Sin conexión", tint = tint)
    }
}

@Composable
fun LoadingState() {
    CircularProgressIndicator()
}

@Composable
fun EmptyState(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun ErrorState(text: String, onRetry: () -> Unit) {
    Column {
        Text(text = text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
        BigActionButton(text = "Reintentar", onClick = onRetry)
    }
}

@Preview(showBackground = true)
@Composable
private fun BigActionButtonPreview() {
    AppTheme { BigActionButton(text = "Iniciar viaje", onClick = {}) }
}

@Preview(showBackground = true)
@Composable
private fun ComponentsPreview() {
    AppTheme {
        Column {
            GpsStatusIcon(hasSignal = true)
            SyncStatusIcon(isOnline = true, recentlySynced = false)
            LoadingState()
            EmptyState("Sin viajes registrados")
        }
    }
}
