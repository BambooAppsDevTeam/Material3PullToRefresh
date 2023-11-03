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
            nexusUrl = uri("https://s01.oss.sonatype.org/service/local/")
            snapshotRepositoryUrl =
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            stagingProfileId = loadSecret(project, "SONATYPE_STAGING_PROFILE_ID")
            username = loadSecret(project, "OSSRH_USERNAME")
            password = loadSecret(project, "OSSRH_PASSWORD")
        }
    }
}

subprojects {
    tasks {
        withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
            jvmTarget = "17"
        }
        withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
            jvmTarget = "17"
        }
    }
}


detekt {
    autoCorrect = true
}
