plugins {
    id("personalappssuite.feature")
}

android {
    namespace = "com.personalapps.suite.workout.feature.progress"
}

dependencies {
    implementation(project(":apps:workout-tracker:feature-api"))
}
