import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    alias(libs.plugins.android.library)
    kotlin("android")
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.dokka)
    publishing
    `maven-publish`
    signing
}

group = "eu.bambooapps"
version = "1.0.1"

android {
    namespace = "eu.bambooapps.material3.pullrefresh"
    compileSdk = 34

    defaultConfig {
        minSdk = 23

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
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

val dokkaJavadoc by tasks.getting(DokkaTask::class)

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(dokkaJavadoc)
    archiveClassifier.set("javadoc")
    from(dokkaJavadoc.outputDirectory)
}

artifacts {
    archives(javadocJar)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "eu.bambooapps"
            artifactId = "compose-material3-pullrefresh"
            version = "1.1.1"

            artifact(javadocJar)

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name = "compose-material3-pullrefresh"
                description = "Material 3 Pull Refresh for Jetpack Compose"
                url = "https://github.com/BambooAppsDevTeam/Material3PullToRefresh"

                licenses {
                    license {
                        name = "Apache-2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                    }
                }

                developers {
                    developer {
                        id = "amv"
                        name = "Andrei Mukamolau"
                        email = "amv@bam-boo.eu"
                    }
                }

                scm {
                    connection = "scm:git:github.com/BambooAppsDevTeam/Material3PullToRefresh.git"
                    developerConnection =
                        "scm:git:ssh://github.com:BambooAppsDevTeam/Material3PullToRefresh.git"
                    url = "https://github.com/BambooAppsDevTeam/Material3PullToRefresh/tree/main"
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        loadSecret(rootProject, "SIGNING_KEY_ID"),
        loadSecret(rootProject, "SIGNING_KEY"),
        loadSecret(rootProject, "SIGNING_PASSWORD")
    )
    sign(publishing.publications)
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    debugImplementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.compose.ui)
    implementation(libs.compose.animations)
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
    dokkaPlugin(libs.dokka.android)
}
