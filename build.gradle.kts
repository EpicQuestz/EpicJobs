plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version("7.0.0")
}

java.targetCompatibility = JavaVersion.VERSION_17
java.sourceCompatibility = JavaVersion.VERSION_17

val pluginGroup: String by extra
val pluginVersion: String by extra

val cloudVersion = "1.6.1"
val ifVersion = "0.10.3"
val taskchainVersion = "3.7.2"
val paperVersion = "1.17.1-R0.1-SNAPSHOT"

group = pluginGroup
version = pluginVersion

repositories {
    mavenCentral()
    maven { url = uri("https://repo.aikar.co/content/groups/aikar/") } // TaskChain
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") } // Paper
    maven { url = uri("https://repo.incendo.org/content/repositories/snapshots") } // Command Framework
}

dependencies {
    implementation("cloud.commandframework:cloud-paper:$cloudVersion")
    implementation("cloud.commandframework:cloud-annotations:$cloudVersion")
    implementation("cloud.commandframework:cloud-minecraft-extras:$cloudVersion") {
        exclude("net.kyori")
    }
    implementation("com.github.stefvanschie.inventoryframework:IF:$ifVersion")
    implementation("co.aikar:taskchain-bukkit:$taskchainVersion")

    implementation("org.checkerframework:checker-qual:3.21.0")
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.3")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.5")

    compileOnly("io.papermc.paper:paper-api:$paperVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    relocate("cloud.commandframework", "de.stealwonders.epicjobs.commandframework")
    relocate("com.github.stefvanschie.inventoryframework", "de.stealwonders.epicjobs.inventoryframework")
    relocate("com.zaxxer.hikari", "de.stealwonders.epicjobs.hikari")
    relocate("org.mariadb.jdbc", "de.stealwonders.epicjobs.jdbc")
//    destinationDirectory = file("server/plugins")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(16)
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to version)
    }
}