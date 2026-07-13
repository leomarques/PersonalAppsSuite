plugins {
    id("personalappssuite.feature")
}

android {
    namespace = "com.personalapps.suite.nutrition.feature.history"
}

dependencies {
    implementation(project(":apps:nutrition-tracker:feature-api"))
}
