plugins {
    id("personalappssuite.feature")
    id("personalappssuite.room")
}

android {
    namespace = "com.personalapps.suite.cannabis.feature.stats"
}

dependencies {
    implementation(project(":apps:cannabis-tracker:feature-api"))
}
