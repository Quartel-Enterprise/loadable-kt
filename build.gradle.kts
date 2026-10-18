import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.vanniktech.maven.publish)
}

group = "software.quare"
version = "1.0.0"

kotlin {
    explicitApi()

    // Android consumers use the JVM artifact, so target bytecode every supported Android runtime reads
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    js {
        nodejs()
    }
    wasmJs {
        nodejs()
    }
    iosArm64()
    iosSimulatorArm64()
    macosArm64()
    linuxX64()
    linuxArm64()
    mingwX64()

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    coordinates(
        groupId = group.toString(),
        artifactId = "loadable",
        version = version.toString(),
    )

    pom {
        name = "Loadable"
        description = "A Kotlin Multiplatform type for values that are either still loading or loaded."
        inceptionYear = "2026"
        url = "https://github.com/Quartel-Enterprise/loadable"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        developers {
            developer {
                id = "Quartel-Enterprise"
                name = "Quartel Enterprise"
                email = "quare.software@gmail.com"
            }
        }

        scm {
            connection = "scm:git:github.com/Quartel-Enterprise/loadable.git"
            developerConnection = "scm:git:ssh://github.com/Quartel-Enterprise/loadable.git"
            url = "https://github.com/Quartel-Enterprise/loadable/tree/main"
        }
    }
}
