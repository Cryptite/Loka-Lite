import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
  `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.3.8"
    id("xyz.jpenilla.run-paper") version "1.0.6" // Adds runServer and runMojangMappedServer tasks for testing
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

group = "com.cryptite.lite"
version = "2.1"
description = "LokaLite Plugin"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    paperDevBundle("1.19.2-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.8")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.6.0")
    compileOnly("com.lokamc:LokaLib:2.2")
    compileOnly("com.lokamc:LokaCore:2.3:dev-all")
}

tasks {
  // Run reobfJar on build
  build {
    dependsOn(reobfJar)
  }

  compileJava {
    options.encoding = Charsets.UTF_8.name()
      options.release.set(17)
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }
}

bukkit {
  load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    main = "com.cryptite.lite.LokaLite"
    apiVersion = "1.19"
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