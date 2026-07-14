plugins {
    id("personalappssuite.android.application")
    id("personalappssuite.compose")
    id("personalappssuite.room")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.personalapps.suite.cannabis"
    defaultConfig {
        applicationId = "com.personalapps.suite.cannabis"
    }
}

dependencies {
    // Shared
    implementation(project(":shared:common"))
    implementation(project(":shared:design-system"))
    implementation(project(":shared:ui-components"))
    implementation(project(":shared:navigation"))
    implementation(project(":shared:database-utils"))
    implementation(project(":shared:preferences"))
    implementation(project(":shared:backup"))

    // Features
    implementation(project(":apps:cannabis-tracker:feature-api"))
    implementation(project(":apps:cannabis-tracker:feature-sessions"))
    implementation(project(":apps:cannabis-tracker:feature-history"))
    implementation(project(":apps:cannabis-tracker:feature-stats"))

    // Navigation3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // Koin DI
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Tests
    testImplementation(project(":shared:testing"))
}
