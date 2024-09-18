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
    repositories {
        mavenLocal()
        maven {
            url = uri("https://maven.pkg.github.com/Nyayurn/Yutori")
            credentials {
                username = "Nyayurn"
                password = System.getenv("GITHUB_PERSONAL_TOKEN")
            }
        }
        google()
        mavenCentral()
    }
}