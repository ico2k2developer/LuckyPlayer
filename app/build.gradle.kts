plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "it.developing.ico2k2.luckyplayer"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "it.developing.ico2k2.luckyplayer"
        minSdk = 24
        targetSdk = 36
        multiDexEnabled = true
        versionCode = 29
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }



    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    annotationProcessor(libs.room.compiler)
    implementation(libs.core)
    implementation(libs.fragment)
    implementation(libs.cardview)
    implementation(libs.palette)
    implementation(libs.recyclerview)
    implementation(libs.preference)
    implementation(libs.annotation)
    implementation(libs.systembartint)
    implementation(libs.jaudiotagger)
    implementation(libs.media)
    implementation(libs.work.runtime)
    implementation(libs.annotations)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.multidex)
    implementation(libs.exoplayer)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}