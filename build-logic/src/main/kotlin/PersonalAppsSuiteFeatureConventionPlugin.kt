import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class PersonalAppsSuiteFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            with(pluginManager) {
                apply("personalappssuite.android.library")
                apply("personalappssuite.compose")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            dependencies {
                add("implementation", project(":shared:common"))
                add("implementation", project(":shared:design-system"))
                add("implementation", project(":shared:ui-components"))
                add("implementation", project(":shared:navigation"))
                add("implementation", project(":shared:database-utils"))

                // Koin
                val koinBom = libs.findLibrary("koin-bom").get()
                add("implementation", platform(koinBom))
                add("implementation", libs.findLibrary("koin-core").get())
                add("implementation", libs.findLibrary("koin-compose").get())

                // Navigation3
                add("implementation", libs.findLibrary("androidx-navigation3-runtime").get())
                add("implementation", libs.findLibrary("androidx-navigation3-ui").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-navigation3").get())

                // Serialization
                add("implementation", libs.findLibrary("kotlinx-serialization-json").get())

                // Testing
                add("testImplementation", project(":shared:testing"))
            }
        }
    }
}
