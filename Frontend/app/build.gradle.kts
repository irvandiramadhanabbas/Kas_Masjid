plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.frontend"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.frontend"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    // kalau nanti butuh:
    // composeOptions {
    //     kotlinCompilerExtensionVersion = "x.x.x"
    // }
}

dependencies {

    // ==== sudah dari template ====
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ==== TAMBAHAN BUAT BACKEND NYAMBUNG ====

    // Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // OkHttp logging (buat lihat request/response di Logcat)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ViewModel (buat AuthViewModel, DashboardViewModel)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    // Compose + ViewModel integration (collectAsStateWithLifecycle, dll)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // DataStore (nanti buat nyimpen token)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.5")

    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")

// supaya enak untuk Navigation Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

// untuk @HiltViewModel (AndroidX)
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation ("androidx.paging:paging-compose:3.3.2")

}

kapt {
    correctErrorTypes = true
}

