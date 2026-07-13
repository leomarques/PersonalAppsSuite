plugins {
    id("personalappssuite.android.library")
    id("personalappssuite.room")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.personalapps.suite.shared.database"
}

dependencies {
    implementation(project(":shared:common"))
    implementation(libs.kotlinx.serialization.json)
    testImplementation(project(":shared:testing"))
}
