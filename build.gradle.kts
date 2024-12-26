plugins {
    id("java")
}

group = "org.pointsPlugin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot API
    maven("https://papermc.io/repo/repository/maven-public/") // Paper
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    implementation("org.java-websocket:Java-WebSocket:1.5.3")
}