import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class PersonalAppsSuiteAndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            with(pluginManager) {
                apply("com.android.application")
                apply("personalappssuite.kotlin")
            }

            extensions.configure<ApplicationExtension> {
                compileSdk = AndroidConfig.COMPILE_SDK
                defaultConfig {
                    minSdk = AndroidConfig.MIN_SDK
                    targetSdk = AndroidConfig.TARGET_SDK
                    versionCode = 1
                    versionName = "1.0"
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                    isCoreLibraryDesugaringEnabled = true
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
                add("coreLibraryDesugaring", libs.findLibrary("android-desugarJdkLibs").get())
            }
        }
    }
}
