plugins {
    id("java-library")
    id("com.gradleup.shadow") version("8.3.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

group = "com.epicquestz"
version = "1.4"

val paperVersion = "1.21.11-R0.1-SNAPSHOT"
val hikariVersion = "6.3.3"
val mariadbClientVersion = "3.5.8"
val cloudVersion = "2.0.0" // cloud-core / annotations
val cloudMinecraftVersion = "2.0.0-beta.9" // cloud-paper / minecraft-extras
val taskchainVersion = "3.7.2"
val inventoryFrameworkVersion = "0.10.13"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") } // Paper
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") } // Cloud
    maven { url = uri( "https://repo.aikar.co/content/groups/aikar/") } // TaskChain
    maven { url = uri("https://www.iani.de/nexus/content/groups/public") } // InventoryFramework
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.mariadb.jdbc:mariadb-java-client:$mariadbClientVersion")
    implementation("org.incendo:cloud-paper:$cloudMinecraftVersion") {
        exclude("org.checkerframework")
    }
    implementation("org.incendo:cloud-annotations:$cloudVersion") {
        exclude("org.checkerframework")
    }
    implementation("org.incendo:cloud-minecraft-extras:$cloudMinecraftVersion") {
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
        val props = mapOf("version" to version)
        inputs.properties(props)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}
