plugins {
    alias(libs.plugins.android.app)
    kotlin("android")
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "eu.bambooapps.material3.pullrefresh.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "eu.bambooapps.material3.pullrefresh.sample"
        minSdk = 23
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
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    debugImplementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(project(":compose-material3-pullrefresh"))

    implementation(libs.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.ui.tooling)
    coreLibraryDesugaring(libs.desugar)

    androidTestImplementation(libs.androidx.test)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.compose.ui.test)

    debugImplementation(libs.compose.ui.test.manifest)
    debugImplementation(libs.compose.ui.tooling)

    testImplementation(libs.junit)

    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.compose.rules)
}
