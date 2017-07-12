package com.cryptite.lite;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigFile {
    Logger log = Logger.getLogger("Artifact-Config");
    private final String fileName;
    private final LokaLite plugin;

    public File configFile;
    private FileConfiguration fileConfiguration;

    public ConfigFile(LokaLite plugin, String fileName) {
        if (plugin == null)
            throw new IllegalArgumentException("plugin cannot be null");
        this.plugin = plugin;
        this.fileName = fileName;
        File dataFolder = plugin.getDataFolder();
        if (dataFolder == null)
            throw new IllegalStateException();
        this.configFile = new File(plugin.getDataFolder(), fileName);
    }

    public String get(String key, Object defaultValue) {
        String value = getConfig().getString(key);
        if (value != null) {
            return value;
        } else {
            if (defaultValue == null) {
                //Because I want strings back, I can't return a string called null, so manually doing it.
                return null;
            } else {
                //Return the variable default value as string.
                return defaultValue.toString();
            }
        }
    }

    public Set<String> getAll(String key, Object defaultValue) {
        ConfigurationSection section = getConfig().getConfigurationSection(key);
        if (section != null && section.getKeys(false) != null) {
            return section.getKeys(false);
        } else {
            return null;
        }
    }

    public List<String> getAllAsList(String key, Object defaultValue) {
        ConfigurationSection section = getConfig().getConfigurationSection(key);
        if (section != null && section.getKeys(false) != null) {
            return (List<String>) section.getKeys(false);
        } else {
            return null;
        }
    }

    public void set(String key, Object value) {
        getConfig().set(key, value);
    }

    void reloadConfig() {
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);

        // Look for defaults in the jar
        InputStream defConfigStream = plugin.getResource(fileName);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            fileConfiguration.setDefaults(defConfig);
        }
    }

    FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            this.reloadConfig();
        }
        return fileConfiguration;
    }

    public void save() {
        if (fileConfiguration != null && configFile != null) {
            try {
                getConfig().save(configFile);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
            }
        }
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            this.plugin.saveResource(fileName, false);
        }
    }

}