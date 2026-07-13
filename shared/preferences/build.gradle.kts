plugins {
    id("personalappssuite.android.library")
}

android {
    namespace = "com.personalapps.suite.shared.preferences"
}

dependencies {
    implementation(project(":shared:common"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.datastore.preferences)
}
