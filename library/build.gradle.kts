plugins {
    alias(libs.plugins.android.library)
    kotlin("android")
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    publishing
    `maven-publish`
    signing
}

group = "eu.bambooapps"
version = "1.0.0"

android {
    namespace = "eu.bambooapps.material3.pullrefresh"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        aarMetadata {
            minCompileSdk = 29
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    testFixtures {
        enable = true
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "eu.bambooapps"
            artifactId = "compose-material3-pullrefresh"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    debugImplementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.preview)
    testImplementation(libs.junit)
    debugImplementation(libs.compose.ui.test.manifest)
    debugImplementation(libs.compose.ui.tooling)
    androidTestImplementation(libs.compose.ui.test)
    androidTestImplementation(libs.androidx.test)
    androidTestImplementation(libs.espresso)
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.compose.rules)
}
