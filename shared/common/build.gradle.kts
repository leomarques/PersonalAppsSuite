plugins {
    id("personalappssuite.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.personalapps.suite.shared.common"
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(project(":shared:testing"))
}
