import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "org.flageolett"
version = "2.0.1"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
        jetbrainsRuntime()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaUltimate("2024.3.1")
        testFramework(TestFrameworkType.Platform)
        instrumentationTools()
        bundledPlugin("JavaScript")
    }

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.assertj:assertj-core:3.25.3")
}

sourceSets {
    main {
        java {
            srcDirs("src")
        }
        resources {
            srcDirs("resources")
        }
    }
    test {
        java {
            srcDirs("tests")
        }
        resources {
            srcDirs("testData")
        }
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "NodeConfig"
        version

        ideaVersion {
            sinceBuild = "232"
            untilBuild = "243.*"
        }
    }

    publishing {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    signing {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }
}

val runIdePlatformTypes =
    listOf(
        IntelliJPlatformType.IntellijIdeaUltimate,
//        IntelliJPlatformType.WebStorm,
    )

runIdePlatformTypes.forEach { platformType ->
    intellijPlatformTesting.runIde.register("run${platformType.name}") {

        plugins {
            disablePlugin("sass")
        }
    }
}