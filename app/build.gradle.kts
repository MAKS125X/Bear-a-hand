import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.gradle.ktlint)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.example.simbirsoftmobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.simbirsoftmobile"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

ktlint {
    android = true
    ignoreFailures = false
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.SARIF)
        reporter(ReporterType.CHECKSTYLE)
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":ui"))
    implementation(project(":data"))
    implementation(project(":api"))
    implementation(project(":local"))
    implementation(project(":network"))
    implementation(project(":common"))
    implementation(project(":feature:help"))
    implementation(project(":feature:news"))
    implementation(project(":feature:event_details"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:content_holder"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:search"))
    implementation(project(":feature:history"))
    implementation(project(":feature:category_settings"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    testImplementation(libs.mockito.core)

    implementation(libs.androidx.fragment.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.rx3)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.coil)
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
}
