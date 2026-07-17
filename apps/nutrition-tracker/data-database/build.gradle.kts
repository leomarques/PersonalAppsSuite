plugins {
    id("personalappssuite.android.library")
    id("personalappssuite.compose")
    id("personalappssuite.room")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.personalapps.suite.nutrition.data.database"
}

dependencies {
    implementation(project(":shared:common"))
    implementation(project(":shared:database-utils"))
    implementation(project(":apps:nutrition-tracker:feature-api"))
    implementation(project(":apps:nutrition-tracker:feature-food"))
    implementation(project(":apps:nutrition-tracker:feature-meals"))
    implementation(project(":apps:nutrition-tracker:feature-macros"))
    implementation(project(":apps:nutrition-tracker:feature-history"))

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
}
