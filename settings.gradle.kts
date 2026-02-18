pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Biocube"
include(":app")
include(":features:auth-face")
include(":features:auth-eye")
include(":features:auth-voice")
include(":features:auth-palm")
include(":features:auth-fingerprint")
include(":core-data")
include(":core-ui")
include(":features:login")
include(":features:navigation")
include(":features:profile")
include(":features:services")
include(":features:splash")
include(":features:user-trainings")
