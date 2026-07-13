plugins {
    id("personalappssuite.android.library")
    id("personalappssuite.compose")
}

android {
    namespace = "com.personalapps.suite.shared.designsystem"
}

dependencies {
    implementation(project(":shared:common"))
    implementation(libs.androidx.core.ktx)
}
