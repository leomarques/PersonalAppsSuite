plugins {
    id("personalappssuite.feature")
    id("personalappssuite.room")
}

android {
    namespace = "com.personalapps.suite.cannabis.feature.history"
}

dependencies {
    implementation(project(":apps:cannabis-tracker:feature-api"))
}
