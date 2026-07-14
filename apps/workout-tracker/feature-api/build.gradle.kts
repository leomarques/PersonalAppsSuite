plugins {
    id("personalappssuite.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.personalapps.suite.workout.feature.api"
}

dependencies {
    implementation(project(":shared:common"))
    implementation(project(":shared:navigation"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
}
