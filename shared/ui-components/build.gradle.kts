plugins {
    id("personalappssuite.android.library")
    id("personalappssuite.compose")
}

android {
    namespace = "com.personalapps.suite.shared.uicomponents"
}

dependencies {
    implementation(project(":shared:common"))
    implementation(project(":shared:design-system"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}
