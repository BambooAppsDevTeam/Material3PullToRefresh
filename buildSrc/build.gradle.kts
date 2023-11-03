plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.dotenv)
}
