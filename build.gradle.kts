import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
  `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.4"
    id("xyz.jpenilla.run-paper") version "2.3.1" // Adds runServer and runMojangMappedServer tasks for testing
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

group = "com.cryptite.lite"
version = "2.4"
description = "LokaLite Plugin"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    paperweightDevBundle("com.lokamc.slice", "1.21.3-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT")
    compileOnly("com.lokamc:LokaLib:2.7")
    compileOnly("com.lokamc:LokaCore:2.7:dev-all")
}

tasks {
  // Run reobfJar on build
  build {
    dependsOn(reobfJar)
  }

  compileJava {
      options.encoding = Charsets.UTF_8.name()
      options.release.set(21)

      val compilerArgs = options.compilerArgs
      compilerArgs.add("-parameters")
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }
}

bukkit {
  load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    main = "com.cryptite.lite.LokaLite"
    apiVersion = "1.21"
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