// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.app) apply false
    alias(libs.plugins.android.library) apply false
    kotlin("android") version libs.versions.kotlin apply false
    alias(libs.plugins.detekt)
}
