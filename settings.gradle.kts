rootProject.name = "YutoriApplication"
include(":application")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        val actor = providers.gradleProperty("gpr.actor").orNull ?: System.getenv("GITHUB_ACTOR")
        val token = providers.gradleProperty("gpr.token").orNull ?: System.getenv("GITHUB_TOKEN")
        maven {
            url = uri("https://maven.pkg.github.com/Nyayurn/yutori")
            credentials {
                username = actor
                password = token
            }
        }
        maven {
            url = uri("https://maven.pkg.github.com/Nyayurn/yutorix-satori")
            credentials {
                username = actor
                password = token
            }
        }
        google()
        mavenCentral()
    }
}