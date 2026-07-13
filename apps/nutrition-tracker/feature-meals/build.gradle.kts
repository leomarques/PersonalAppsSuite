plugins {
    id("personalappssuite.android.library")
    id("personalappssuite.compose")
    id("personalappssuite.room")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.personalapps.suite.nutrition.feature.meals"
}

dependencies {
    implementation(project(":shared:common"))
    implementation(project(":shared:design-system"))
    implementation(project(":shared:ui-components"))
    implementation(project(":shared:navigation"))
    implementation(project(":shared:database-utils"))
    implementation(project(":apps:nutrition-tracker:feature-food"))

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.kotlinx.serialization.json)

    // Navigation3
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    testImplementation(project(":shared:testing"))
}
