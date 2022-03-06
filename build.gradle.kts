plugins {
    kotlin("jvm") version "1.6.0"
}

repositories {
    mavenCentral()
}

tasks {
    /*sourceSets {
        main {
            java.srcDirs("src/main/kotlin")
            resources.srcDirs("src/main/resources")
        }
    }*/

    wrapper {
        gradleVersion = "7.3"
    }
}
