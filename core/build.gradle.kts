plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":utils:result"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.dagger)
}