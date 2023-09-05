// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.app) apply false
    alias(libs.plugins.android.library) apply false
    kotlin("android") version libs.versions.kotlin apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.publishing)
    alias(libs.plugins.dokka)
}

nexusPublishing {
    this.repositories {
       sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
