pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SimbirSoftMobile"
include(":app")
include(":core")
include(":utils:result")
include(":utils:mapper")
include(":network")
include(":api")
include(":local")
include(":data")
include(":utils:date")
include(":ui")
include(":utils:data_error")
include(":feature:auth")
include(":feature:news")
include(":common_view")
include(":feature:event_details")
include(":feature:profile")
include(":feature:history")
include(":feature:help")
include(":feature:category_settings")
include(":feature:search")
include(":feature:content_holder")
include(":basics")
include(":common_compose")
