plugins {
    id("personalappssuite.android.library")
    id("personalappssuite.compose")
    id("personalappssuite.room")
}

android {
    namespace = "com.personalapps.suite.nutrition.feature.food"
}

dependencies {
    implementation(project(":shared:common"))
    implementation(project(":shared:design-system"))
    implementation(project(":shared:ui-components"))
    implementation(project(":shared:navigation"))
    implementation(project(":shared:database-utils"))

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.compose)

    testImplementation(project(":shared:testing"))
}
