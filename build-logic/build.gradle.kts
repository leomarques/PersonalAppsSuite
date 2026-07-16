plugins {
    `kotlin-dsl`
}

group = "com.personalapps.suite.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kotlin") {
            id = "personalappssuite.kotlin"
            implementationClass = "PersonalAppsSuiteKotlinConventionPlugin"
        }
        register("androidApplication") {
            id = "personalappssuite.android.application"
            implementationClass = "PersonalAppsSuiteAndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "personalappssuite.android.library"
            implementationClass = "PersonalAppsSuiteAndroidLibraryConventionPlugin"
        }
        register("compose") {
            id = "personalappssuite.compose"
            implementationClass = "PersonalAppsSuiteComposeConventionPlugin"
        }
        register("room") {
            id = "personalappssuite.room"
            implementationClass = "PersonalAppsSuiteRoomConventionPlugin"
        }
        register("feature") {
            id = "personalappssuite.feature"
            implementationClass = "PersonalAppsSuiteFeatureConventionPlugin"
        }
    }
}
