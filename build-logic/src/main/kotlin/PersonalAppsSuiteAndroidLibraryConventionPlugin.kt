import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class PersonalAppsSuiteAndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("personalappssuite.kotlin")
            }

            extensions.configure<LibraryExtension> {
                compileSdk = AndroidConfig.COMPILE_SDK
                defaultConfig {
                    minSdk = AndroidConfig.MIN_SDK
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
                buildFeatures {
                    aidl = false
                    buildConfig = false
                    shaders = false
                }
                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
            }
            dependencies {
            }
        }
    }
}
