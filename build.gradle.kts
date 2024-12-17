plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot repository
    maven("https://papermc.io/repo/repository/maven-public/") // Paper repository (optional, for Paper API)
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT") // Use Minecraft's current version
    // Or for Paper API:
    // compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}