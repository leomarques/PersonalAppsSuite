plugins {
    id("personalappssuite.android.library")
}

android {
    namespace = "com.personalapps.suite.shared.testing"
}

dependencies {
    implementation(project(":shared:common"))
    api(libs.junit)
    api(libs.kotlinx.coroutines.test)
}
