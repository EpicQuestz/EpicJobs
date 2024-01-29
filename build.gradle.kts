plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

group = "com.epicquestz"
version = "1.4"

val paperVersion = "1.20.4-R0.1-SNAPSHOT"
val hikariVersion = "5.1.0"
val mariadbClientVersion = "2.7.11"
val cloudVersion = "2.0.0-SNAPSHOT"
val taskchainVersion = "3.7.2"
val inventoryFrameworkVersion = "0.10.13"

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") } // Paper
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") } // Cloud
    maven { url = uri( "https://repo.aikar.co/content/groups/aikar/") } // TaskChain
    maven { url = uri("https://www.iani.de/nexus/content/groups/public") } // InventoryFramework
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.mariadb.jdbc:mariadb-java-client:$mariadbClientVersion")
    implementation("org.incendo:cloud-paper:$cloudVersion") {
        exclude("org.checkerframework")
    }
    implementation("org.incendo:cloud-annotations:$cloudVersion") {
        exclude("org.checkerframework")
    }
    implementation("org.incendo:cloud-minecraft-extras:$cloudVersion") {
        exclude("org.checkerframework")
        exclude("net.kyori")
    }
    implementation("co.aikar:taskchain-bukkit:$taskchainVersion")
    implementation("com.github.stefvanschie.inventoryframework:IF:$inventoryFrameworkVersion")
}

tasks {
    shadowJar {
        relocate("com.zaxxer.hikari", "com.epicquestz.epicjobs.hikari")
        relocate("org.mariadb.jdbc", "com.epicquestz.epicjobs.jdbc")
        relocate("org.incendo", "com.epicquestz.epicjobs.commandframework")
        relocate("com.github.stefvanschie.inventoryframework", "com.epicquestz.inventoryframework")
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to version)
        }
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
}
