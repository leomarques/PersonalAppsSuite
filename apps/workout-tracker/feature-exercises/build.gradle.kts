plugins {
    id("personalappssuite.feature")
    id("personalappssuite.room")
}

android {
    namespace = "com.personalapps.suite.workout.feature.exercises"
}

dependencies {
    implementation(project(":apps:workout-tracker:feature-api"))
}
