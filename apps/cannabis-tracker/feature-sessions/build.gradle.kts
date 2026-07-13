plugins {
    id("personalappssuite.feature")
    id("personalappssuite.room")
}

android {
    namespace = "com.personalapps.suite.cannabis.feature.sessions"
}

dependencies {
    implementation(project(":apps:cannabis-tracker:feature-api"))
}
