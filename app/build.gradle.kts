plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}
android {
    namespace = "com.compensatuviaje.tracker"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.compensatuviaje.tracker"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
}
dependencies {
    // Core
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))

    // Features
    implementation(project(":feature:database"))
    implementation(project(":feature:location-service"))
    implementation(project(":feature:sync-worker"))
    implementation(project(":feature:api-client"))
    implementation(project(":feature:trip"))
    implementation(project(":feature:distance"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:session"))
    implementation(project(":feature:vehicle"))
    implementation(project(":feature:connectivity"))
    implementation(project(":feature:map-google"))
    implementation(project(":feature:map-osm"))
    implementation(project(":feature:history"))
    implementation(project(":feature:trip-detail"))
    implementation(project(":feature:stats"))
    implementation(project(":feature:export"))
    implementation(project(":feature:auth-firebase"))
    implementation(project(":feature:cloud-mirror"))

    // Android + Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.ui.tooling)
}
