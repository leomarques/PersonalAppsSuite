plugins {
    id("personalappssuite.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.personalapps.suite.shared.backup"
}

dependencies {
    implementation(project(":shared:common"))
    implementation(libs.kotlinx.serialization.json)
}
