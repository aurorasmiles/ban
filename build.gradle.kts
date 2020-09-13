import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow") version "6.0.0"
    // TODO: Licence plugin
}

group = "com.proximyst.ban"
version = "0.1.0"

repositories {
    maven {
        name = "aikar"
        url = uri("https://repo.aikar.co/content/groups/aikar/")

        content {
            includeGroup("co.aikar")
        }
    }

    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")

        content {
            includeGroup("net.kyori")
        }
    }

    maven {
        name = "sewer"
        url = uri("https://dl.bintray.com/proximyst/sewer")

        content {
            includeGroup("com.proximyst")
        }
    }

    maven {
        name = "velocity"
        url = uri("https://repo.velocitypowered.com/snapshots/")
    }

    jcenter()
    mavenCentral()
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:1.1.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:1.1.0-SNAPSHOT")

    implementation("org.jdbi:jdbi3-core:3.14.1")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.6.1")
    implementation("com.zaxxer:HikariCP:3.4.5") {
        exclude("org.slf4j")
    }

    implementation("net.kyori:adventure-text-minimessage:3.0.0-SNAPSHOT") {
        // We get adventure through velocity-api
        isTransitive = false
    }

    implementation("com.proximyst:sewer:0.5.0")

    implementation("net.time4j:time4j-base:5.6")
    implementation("net.time4j:time4j-sqlxml:5.6")
    implementation("net.time4j:time4j-tzdata:5.0-2020a")
}

tasks {
    withType<ShadowJar> {
        fun reloc(vararg dependencies: String) =
            dependencies.forEach { relocate(it, "com.proximyst.ban.dependencies.$it") }

        reloc(
            "co.aikar.commands",
            "co.aikar.locale",
            "com.zaxxer.hikari",
            "org.mariadb.jdbc",
            "net.kyori.adventure.text.minimessage",
            "org.jdbi",
            "com.proximyst.sewer",
            "net.time4j"
        )
        mergeServiceFiles()
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = sourceCompatibility
}
