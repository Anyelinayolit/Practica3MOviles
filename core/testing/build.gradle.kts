plugins {
    alias(libs.plugins.kotlin.jvm)
}
kotlin { jvmToolchain(17) }
dependencies {
    api(project(":core:model"))
    api(project(":core:domain"))
    api(libs.kotlinx.coroutines.core)
    api(libs.mockwebserver)
    api(libs.junit)
    api(libs.truth)
    api(libs.turbine)
    api(libs.kotlinx.coroutines.test)
}
