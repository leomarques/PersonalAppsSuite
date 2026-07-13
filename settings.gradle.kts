pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "PersonalAppsSuite"

// Shared Modules
include(":shared:common")
include(":shared:design-system")
include(":shared:ui-components")
include(":shared:navigation")
include(":shared:database-utils")
include(":shared:preferences")
include(":shared:backup")
include(":shared:testing")

// Nutrition Tracker App Modules
include(":apps:nutrition-tracker:app")
include(":apps:nutrition-tracker:feature-api")
include(":apps:nutrition-tracker:feature-food")
include(":apps:nutrition-tracker:feature-meals")
include(":apps:nutrition-tracker:feature-macros")
include(":apps:nutrition-tracker:feature-history")

// Workout Tracker App Modules
include(":apps:workout-tracker:app")
include(":apps:workout-tracker:feature-api")
include(":apps:workout-tracker:feature-exercises")
include(":apps:workout-tracker:feature-workouts")
include(":apps:workout-tracker:feature-progress")

// Cannabis Tracker App Modules
include(":apps:cannabis-tracker:app")
include(":apps:cannabis-tracker:feature-api")
include(":apps:cannabis-tracker:feature-sessions")
include(":apps:cannabis-tracker:feature-history")
include(":apps:cannabis-tracker:feature-stats")
