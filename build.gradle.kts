import org.gradle.api.publish.PublishingExtension
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
  `java-library`
  `maven-publish`
  id("io.papermc.paperweight.userdev") version "1.1.11"
  id("xyz.jpenilla.run-paper") version "1.0.4" // Adds runServer and runMojangMappedServer tasks for testing
  id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
}

group = "com.cryptite.lite"
version = "2.0"
description = "LokaLite Plugin"

repositories {
    mavenCentral()
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven {
        url = uri("http://ysera.dyndns.org:8090/releases")
        isAllowInsecureProtocol = true
    }
}

dependencies {
    paperDevBundle("1.17.1-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.6.0")
    compileOnly("com.lokamc.LokaLib:LokaLib:2.0")
    compileOnly("com.lokamc.LokaCore:LokaCore:2.0:dev-all")
}

tasks {
  // Run reobfJar on build
  build {
    dependsOn(reobfJar)
  }

  compileJava {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(16)
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }
}

bukkit {
  load = BukkitPluginDescription.PluginLoadOrder.STARTUP
  main = "com.cryptite.lite.LokaLite"
  apiVersion = "1.17"
  authors = listOf("Cryptite")
  depend = listOf("LokaLib", "LokaCore", "Multiverse-Core")
  commands {
          register("leave") {
              description = "Leave!"
              // permissionMessage = "You may not test this command!"
          }
          register("shutdown") {
              description = "Sends all players back to Loka before restart"
              // permissionMessage = "You may not test this command!"
          }
      }
}