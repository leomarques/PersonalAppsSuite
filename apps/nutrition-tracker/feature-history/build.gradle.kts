plugins {
    id("personalappssuite.feature")
    id("personalappssuite.room")
}

android {
    namespace = "com.personalapps.suite.nutrition.feature.history"
}

dependencies {
    implementation(project(":apps:nutrition-tracker:feature-api"))
    implementation(project(":shared:preferences"))
}
