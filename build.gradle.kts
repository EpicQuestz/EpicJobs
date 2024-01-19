plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version("8.1.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

group = "de.stealwonders"
version = "1.2"

val paperVersion = "1.20.4-R0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") } // Paper
    maven { url = uri( "https://repo.aikar.co/content/groups/aikar/") }
    maven { url = uri("https://repo.destroystokyo.com/repository/maven-public/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://www.iani.de/nexus/content/groups/public") }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
    compileOnly("com.google.guava:guava:28.0-jre")
    compileOnly("de.iani.cubeside:PlayerUUIDCache:1.5.1-SNAPSHOT")
    implementation("com.zaxxer:HikariCP:3.4.2")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.6.0")
    implementation("co.aikar:acf-paper:0.5.0-SNAPSHOT")
    implementation("co.aikar:taskchain-bukkit:3.7.2")
    implementation("net.kyori:text-api:3.0.4")
    implementation("net.kyori:text-serializer-gson:3.0.4")
    implementation("net.kyori:text-adapter-bukkit:3.0.5")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.6.1")
}

tasks {
//    compileJava {
//        options.compilerArgs += ["-parameters"]
//        options.fork = true
//        options.forkOptions.executable = 'javac'
//    }

    shadowJar {
        relocate("com.zaxxer.hikari", "de.stealwonders.epicjobs.hikari")
        relocate("org.mariadb.jdbc", "de.stealwonders.epicjobs.jdbc")
        relocate("co.aikar.commands", "de.stealwonders.epicjobs.acf")
        relocate("com.github.stefvanschie.inventoryframework", "org.empirewar.battlegrounds.inventoryframework")
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
