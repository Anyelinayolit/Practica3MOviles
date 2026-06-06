package com.compensatuviaje.tracker.feature.tripdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compensatuviaje.tracker.domain.TripRepository
import com.compensatuviaje.tracker.model.Trip
import com.compensatuviaje.tracker.model.TripStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

// =======================================================
// 1. ESTADOS DE LA UI
// =======================================================
sealed interface TripDetailUiState {
    object Loading : TripDetailUiState
    data class Success(val trip: Trip, val durationText: String) : TripDetailUiState
    object ErrorNotFound : TripDetailUiState
}

// =======================================================
// 2. VIEWMODEL
// =======================================================
class TripDetailViewModel(
    private val tripRepository: TripRepository,
    private val tripId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<TripDetailUiState>(TripDetailUiState.Loading)
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    init {
        loadTripDetails()
    }

    private fun loadTripDetails() {
        viewModelScope.launch {
            try {
                val trip = tripRepository.get(tripId)
                if (trip != null) {
                    val durationText = calculateDuration(trip.startedAtIso, trip.endedAtIso)
                    _uiState.value = TripDetailUiState.Success(trip, durationText)
                } else {
                    _uiState.value = TripDetailUiState.ErrorNotFound
                }
            } catch (e: Exception) {
                _uiState.value = TripDetailUiState.ErrorNotFound
            }
        }
    }

    private fun calculateDuration(startIso: String, endIso: String?): String {
        if (endIso == null) return "En progreso"
        return try {
            val start = Instant.parse(startIso)
            val end = Instant.parse(endIso)
            val duration = Duration.between(start, end)
            val hours = duration.toHours()
            val minutes = duration.toMinutes() % 60
            if (hours > 0) "${hours}h ${minutes}m" else "${minutes} min"
        } catch (e: Exception) {
            "-- min"
        }
    }
}

// =======================================================
// 3. PANTALLA EN JETPACK COMPOSE
// =======================================================
@Composable
fun TripDetailScreen(
    tripId: String,
    viewModel: TripDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val fakeTripRepository = object : TripRepository {
                    override fun activeTrip(): Flow<Trip?> = flowOf(null)
                    override fun completedTrips(): Flow<List<Trip>> = flowOf(emptyList())
                    override suspend fun create(trip: Trip) {}
                    override suspend fun update(trip: Trip) {}
                    override suspend fun setStatus(tripId: String, status: TripStatus) {}
                    override suspend fun get(tripId: String): Trip? {
                        return if (tripId == "preview_id") {
                            Trip(
                                id = "preview_id",
                                status = TripStatus.COMPLETED,
                                startedAtIso = "2026-06-05T08:00:00Z",
                                endedAtIso = "2026-06-05T09:30:00Z",
                                totalLocalDistanceKm = 42.8,
                                isSyncedToServer = true,
                                co2Kg = 8.4
                            )
                        } else null
                    }
                }
                return TripDetailViewModel(fakeTripRepository, tripId) as T
            }
        }
    ),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("Resumen del Viaje") })
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
                is TripDetailUiState.Loading -> CircularProgressIndicator()

                is TripDetailUiState.ErrorNotFound -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: El viaje seleccionado no existe.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(text = "ID buscado: $tripId", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                is TripDetailUiState.Success -> {
                    val trip = state.trip
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🗺️ [Mini-mapa de la Ruta]", style = MaterialTheme.typography.titleMedium)
                                Text("Segmentos cargados para ID: ${trip.id}", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(text = "Métricas del Recorrido", style = MaterialTheme.typography.titleMedium)
                                HorizontalDivider()

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Distancia Total:", style = MaterialTheme.typography.bodyMedium)
                                    Text("${trip.totalLocalDistanceKm} km", style = MaterialTheme.typography.bodyLarge)
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Duración Estimada:", style = MaterialTheme.typography.bodyMedium)
                                    Text(state.durationText, style = MaterialTheme.typography.bodyLarge)
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Huella CO₂ Calculada:", style = MaterialTheme.typography.bodyMedium)
                                    Text("${trip.co2Kg ?: 0.0} kg", style = MaterialTheme.typography.bodyLarge)
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Fecha de Salida:", style = MaterialTheme.typography.bodyMedium)
                                    Text(trip.startedAtIso.substringBefore("T"), style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}