plugins {
    `kotlin-dsl`
}

group = "com.personalapps.suite.buildlogic"

dependencies {
    compileOnly("com.android.tools.build:gradle:9.0.1")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20")
    compileOnly("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:2.3.6")
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
    }
}
