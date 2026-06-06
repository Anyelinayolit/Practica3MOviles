package com.compensatuviaje.tracker.feature.vehicle

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compensatuviaje.tracker.domain.SessionRepository
import com.compensatuviaje.tracker.domain.TripRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// =======================================================
// 1. ESTADOS DE LA UI
// =======================================================
sealed interface VehicleUiState {
    object Loading : VehicleUiState
    data class Success(val plate: String, val category: String) : VehicleUiState
    object EmptyData : VehicleUiState
    object NavigateToHome : VehicleUiState
}

// =======================================================
// 2. VIEWMODEL
// =======================================================
class VehicleViewModel(
    private val sessionRepository: SessionRepository,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<VehicleUiState>(VehicleUiState.Loading)
    val uiState: StateFlow<VehicleUiState> = _uiState.asStateFlow()

    init {
        loadVehicleData()
    }

    private fun loadVehicleData() {
        viewModelScope.launch {
            sessionRepository.current
                .catch { _uiState.value = VehicleUiState.EmptyData }
                .collect { session ->
                    if (session != null && session.truck.licensePlate.isNotBlank()) {
                        _uiState.value = VehicleUiState.Success(
                            plate = session.truck.licensePlate,
                            category = session.truck.category
                        )
                    } else {
                        _uiState.value = VehicleUiState.EmptyData
                    }
                }
        }
    }

    fun confirmVehicle() {
        viewModelScope.launch {
            try {
                // CAMBIO CLAVE PARA LA CONEXIÓN REAL: .first() espera el valor real de la BD de Room
                val currentTrip = tripRepository.activeTrip().first()
                if (currentTrip != null) {
                    tripRepository.update(currentTrip)
                }
                _uiState.value = VehicleUiState.NavigateToHome
            } catch (e: Exception) {
                _uiState.value = VehicleUiState.EmptyData
            }
        }
    }
}

// =======================================================
// 3. PANTALLA EN JETPACK COMPOSE
// =======================================================
@Composable
fun VehicleScreen(
    viewModel: VehicleViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val fakeSession = object : SessionRepository {
                    override val current: Flow<com.compensatuviaje.tracker.model.Session?> = flowOf(
                        com.compensatuviaje.tracker.model.Session(
                            token = "fake_jwt",
                            driverName = "Chofer Flota",
                            truck = com.compensatuviaje.tracker.model.Truck(
                                id = "1",
                                licensePlate = "ABC-123",
                                category = "Carga Pesada"
                            )
                        )
                    )
                    override suspend fun setSession(session: com.compensatuviaje.tracker.model.Session) {}
                    override suspend fun logout() {}
                }
                val fakeTrip = object : TripRepository {
                    override fun activeTrip(): Flow<com.compensatuviaje.tracker.model.Trip?> = flowOf(null)
                    override fun completedTrips(): Flow<List<com.compensatuviaje.tracker.model.Trip>> = flowOf(emptyList())
                    override suspend fun create(trip: com.compensatuviaje.tracker.model.Trip) {}
                    override suspend fun update(trip: com.compensatuviaje.tracker.model.Trip) {}
                    override suspend fun setStatus(tripId: String, status: com.compensatuviaje.tracker.model.TripStatus) {}
                    override suspend fun get(tripId: String): com.compensatuviaje.tracker.model.Trip? = null
                }
                return VehicleViewModel(fakeSession, fakeTrip) as T
            }
        }
    ),
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState is VehicleUiState.NavigateToHome) {
        LaunchedEffect(Unit) {
            onNavigateToHome()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is VehicleUiState.Loading -> {
                CircularProgressIndicator()
            }
            is VehicleUiState.EmptyData -> {
                Text(
                    text = "No se encontraron datos del vehículo activo en la sesión.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            is VehicleUiState.Success -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = "Confirmación de Vehículo",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(text = "Patente / Placa:", style = MaterialTheme.typography.labelLarge)
                            Text(text = state.plate, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Categoría / Tipo:", style = MaterialTheme.typography.labelLarge)
                            Text(text = state.category, style = MaterialTheme.typography.titleMedium)
                        }
                    }

                    Button(
                        onClick = { viewModel.confirmVehicle() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                    ) {
                        Text(
                            text = "Confirmar y Continuar",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            // Evita el error en la interfaz sellada
            else -> {}
        }
    }
}