plugins {
    id("com.android.application") version "8.8.0" apply false
    id("com.android.library") version "8.8.0" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("io.realm:realm-gradle-plugin:10.16.0")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
