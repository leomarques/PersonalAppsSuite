plugins {
    id("personalappssuite.feature")
    id("personalappssuite.room")
}

android {
    namespace = "com.personalapps.suite.nutrition.feature.meals"
}

dependencies {
    implementation(project(":apps:nutrition-tracker:feature-api"))
}
