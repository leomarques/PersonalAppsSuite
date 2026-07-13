plugins {
    id("personalappssuite.android.library")
    id("personalappssuite.compose")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.personalapps.suite.shared.navigation"
}

dependencies {
    implementation(project(":shared:common"))
    api(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}
