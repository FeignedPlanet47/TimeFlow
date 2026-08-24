import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "ru.it.timeflow"
    compileSdk = 36

    defaultConfig {
        applicationId = "ru.it.timeflow"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.11.0")

    implementation(
        platform("androidx.compose:compose-bom:2026.06.00")
    )

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation(
        "androidx.lifecycle:lifecycle-runtime-compose:2.9.4"
    )
    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4"
    )

    implementation(
        "androidx.navigation:navigation-compose:2.9.5"
    )

    implementation(
        "androidx.hilt:hilt-navigation-compose:1.2.0"
    )

    implementation("com.google.dagger:hilt-android:2.58")
    ksp("com.google.dagger:hilt-compiler:2.58")

    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2"
    )
}