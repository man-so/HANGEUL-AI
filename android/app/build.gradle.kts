plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.manso.hangeulai"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.manso.hangeulai"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "0.3.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2025.08.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")

    // LiteRT-LM 0.14.0 is compiled against coroutines 1.11.x. Pinning the
    // runtime avoids a known completion-time NoSuchMethodError on Android.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")

    // Official Google AI Edge LiteRT-LM runtime for local Gemma inference.
    // The 584 MB Gemma model is imported separately and is not bundled in APK.
    implementation("com.google.ai.edge.litertlm:litertlm-android:0.14.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
