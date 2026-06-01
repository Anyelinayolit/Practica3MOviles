package com.compensatuviaje.tracker.feature.tripdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// TODO: Implementar detalle de un viaje (resumen, mapa, distancia, CO2)
@Composable
fun TripDetailScreen(tripId: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("TripDetailScreen($tripId) — TODO")
    }
}
