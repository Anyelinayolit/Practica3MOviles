package com.compensatuviaje.tracker

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.compensatuviaje.tracker.feature.auth.AuthScreen
import com.compensatuviaje.tracker.feature.export.ExportScreen
import com.compensatuviaje.tracker.feature.history.HistoryScreen
import com.compensatuviaje.tracker.feature.mapgoogle.MapGoogleScreen
import com.compensatuviaje.tracker.feature.maposm.MapOsmScreen
import com.compensatuviaje.tracker.feature.onboarding.OnboardingScreen
import com.compensatuviaje.tracker.feature.stats.StatsScreen
import com.compensatuviaje.tracker.feature.trip.TripScreen
import com.compensatuviaje.tracker.feature.tripdetail.TripDetailScreen
import com.compensatuviaje.tracker.feature.vehicle.VehicleScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "history") {
        composable("onboarding") { OnboardingScreen() }
        composable("auth") { AuthScreen() }
        composable("vehicle") { VehicleScreen() }
        composable("trip") { TripScreen() }
        composable("history") { HistoryScreen() }
        composable("stats") { StatsScreen() }
        composable("trip_detail/{tripId}") { backStack ->
            TripDetailScreen(tripId = backStack.arguments?.getString("tripId") ?: "")
        }
        composable("map_google") { MapGoogleScreen() }
        composable("map_osm") { MapOsmScreen() }
        composable("export") { ExportScreen() }
    }
}
