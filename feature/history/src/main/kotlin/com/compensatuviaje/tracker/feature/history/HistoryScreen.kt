package com.compensatuviaje.tracker.feature.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compensatuviaje.tracker.domain.TripRepository
import com.compensatuviaje.tracker.model.Trip
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

// =======================================================
// 1. ESTADOS DE LA UI
// =======================================================
sealed interface HistoryUiState {
    object Loading : HistoryUiState
    object Empty : HistoryUiState // Criterio: Estado vacío
    data class Success(val trips: List<Trip>) : HistoryUiState
}

// =======================================================
// 2. VIEWMODEL
// =======================================================
class HistoryViewModel(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            tripRepository.completedTrips()
                .catch { _uiState.value = HistoryUiState.Empty }
                .collect { tripList ->
                    if (tripList.isEmpty()) {
                        _uiState.value = HistoryUiState.Empty
                    } else {
                        // Criterio: Lista ordenada por fecha (descendente: más nuevos primero)
                        val sortedTrips = tripList.sortedByDescending { it.startedAtIso }
                        _uiState.value = HistoryUiState.Success(sortedTrips)
                    }
                }
        }
    }
}

// =======================================================
// 3. PANTALLA EN JETPACK COMPOSE
// =======================================================
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val fakeTripRepository = object : TripRepository {
                    override fun activeTrip() = flowOf(null)
                    override fun completedTrips(): Flow<List<Trip>> = flowOf(
                        listOf(
                            Trip(id = "local_uuid_777", status = com.compensatuviaje.tracker.model.TripStatus.COMPLETED, startedAtIso = "2026-06-05T08:00:00Z", endedAtIso = "2026-06-05T10:15:00Z", totalLocalDistanceKm = 85.4, isSyncedToServer = true, co2Kg = 15.2),
                            Trip(id = "local_uuid_888", status = com.compensatuviaje.tracker.model.TripStatus.COMPLETED, startedAtIso = "2026-06-04T14:00:00Z", endedAtIso = "2026-06-04T14:45:00Z", totalLocalDistanceKm = 32.1, isSyncedToServer = false)
                        )
                    )
                    override suspend fun create(trip: Trip) {}
                    override suspend fun update(trip: Trip) {}
                    override suspend fun setStatus(tripId: String, status: com.compensatuviaje.tracker.model.TripStatus) {}
                    override suspend fun get(tripId: String): Trip? = null
                }
                return HistoryViewModel(fakeTripRepository) as T
            }
        }
    ),
    modifier: Modifier = Modifier,
    onNavigateToDetail: (String) -> Unit = {} // Criterio: Tap -> detalle
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("Historial de Viajes") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is HistoryUiState.Loading -> CircularProgressIndicator()
                is HistoryUiState.Empty -> {
                    Text("No tienes viajes completados en el historial.", style = MaterialTheme.typography.bodyLarge)
                }
                is HistoryUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.trips) { trip ->
                            // Criterio de Aceptación: Tap -> detalle conectado mediante el onClick
                            TripHistoryItem(trip = trip, onClick = { onNavigateToDetail(trip.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TripHistoryItem(trip: Trip, onClick: () -> Unit) {
    // Cálculo Dinámico de la duración (Criterio: Muestra distancia y duración)
    val durationText = remember(trip.startedAtIso, trip.endedAtIso) {
        if (trip.endedAtIso != null) {
            try {
                val start = Instant.parse(trip.startedAtIso)
                val end = Instant.parse(trip.endedAtIso)
                val duration = Duration.between(start, end)
                val hours = duration.toHours()
                val minutes = duration.toMinutes() % 60
                if (hours > 0) "${hours}h ${minutes}m" else "${minutes} min"
            } catch (e: Exception) {
                "Tiempo no registrado"
            }
        } else {
            "En curso"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // Criterio: Tap -> detalle
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "ID: ${trip.id}", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = trip.startedAtIso.substringBefore("T"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(thickness = 0.5.dp)

            // Criterios cumplidos: Distancia y Duración mostrados explícitamente en paralelo
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Distancia", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${trip.totalLocalDistanceKm} km", style = MaterialTheme.typography.bodyLarge)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Duración", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = durationText, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}