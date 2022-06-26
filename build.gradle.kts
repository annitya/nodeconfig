plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.6.0"
}

group = "org.flageolett"
version = "2.0.1"

repositories {
    mavenCentral()
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

intellij {
    version.set("213.7172.25")
    type.set("IU")
    pluginName.set("NodeConfig")
    plugins.set(listOf("JavaScriptLanguage", "CSS"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("213")
        untilBuild.set("223.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
