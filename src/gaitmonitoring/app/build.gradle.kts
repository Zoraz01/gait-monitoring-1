plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.gaitmonitoring"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gaitmonitoring"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    /*View model*/
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    /** Jetpack data store */
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    // compose navigation ..
    implementation ("androidx.navigation:navigation-compose:2.7.7")


    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.7.0") // collect with life cycle
    implementation ("androidx.core:core-splashscreen:1.0.1")  // splash screen
    implementation ("androidx.compose.ui:ui-util:1.7.0-alpha03")     // fast collections functions
    implementation ("androidx.compose.material3:material3:1.3.0-alpha01")
    implementation ("androidx.lifecycle:lifecycle-process:2.7.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.2")

    //Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")

    implementation ("com.google.code.gson:gson:2.10.1") // serialization from / to string
    implementation("io.coil-kt:coil-compose:2.6.0")     // image loading ..
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0") // mp chart for graph

    // Koin DI for Android
    val koinVersion = "3.5.3"
    implementation ("io.insert-koin:koin-android:$koinVersion")
    implementation ("io.insert-koin:koin-androidx-compose:$koinVersion")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")     // authentication
    implementation("com.google.firebase:firebase-firestore")    // firestore
    implementation("com.google.firebase:firebase-storage")      // storage

    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")   //use local date time before api 26

}