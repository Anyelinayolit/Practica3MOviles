pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google(); mavenCentral()
        maven("https://repo.osmdroid.github.io/")
    }
}

rootProject.name = "compensa-tu-viaje-android"

include(":app")

// Núcleo (provisto, no editar)
include(":core:model", ":core:domain", ":core:common", ":core:testing", ":core:designsystem", ":core:network")

// Módulos de equipo
include(
    ":feature:database", ":feature:location-service", ":feature:sync-worker", ":feature:api-client",
    ":feature:trip", ":feature:distance", ":feature:auth", ":feature:onboarding",
    ":feature:session", ":feature:vehicle", ":feature:connectivity",
    ":feature:map-google", ":feature:map-osm", ":feature:history", ":feature:trip-detail",
    ":feature:stats", ":feature:export", ":feature:auth-firebase", ":feature:cloud-mirror"
)
