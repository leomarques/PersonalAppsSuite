plugins {
    id("personalappssuite.android.library")
    id("personalappssuite.compose")
    id("personalappssuite.room")
}

android {
    namespace = "com.personalapps.suite.workout.feature.workouts"
}

dependencies {
    implementation(project(":shared:common"))
    implementation(project(":shared:design-system"))
    implementation(project(":shared:ui-components"))
    implementation(project(":shared:navigation"))
    implementation(project(":shared:database-utils"))
    implementation(project(":apps:workout-tracker:feature-exercises"))

    // Koin
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.compose)

    testImplementation(project(":shared:testing"))
}
