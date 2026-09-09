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

    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.datastore.preferences)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Koin DI
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Tests
    testImplementation(project(":shared:testing"))
}
