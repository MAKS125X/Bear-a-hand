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
include(":network")
include(":api")
include(":local")
include(":data")
include(":ui")
include(":common_compose")
include(":common_view")

include(":utils:result")
include(":utils:mapper")
include(":utils:date")
include(":utils:permission")
include(":utils:data_error")
include(":utils:test_rules")

include(":feature:auth")
include(":feature:news")
include(":feature:event_details")
include(":feature:profile")
include(":feature:history")
include(":feature:help")
include(":feature:category_settings")
include(":feature:search")
include(":feature:content_holder")
include(":feature:notification")

include(":basics")
