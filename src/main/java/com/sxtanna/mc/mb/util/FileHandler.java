package com.sxtanna.mc.mb.util;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class FileHandler extends YamlConfiguration {
    private final JavaPlugin plugin;

    private final String name;

    private final Boolean parent;

    private File file;

    public FileHandler(JavaPlugin plugin, String name, Boolean parent) throws IOException, InvalidConfigurationException {
        this.plugin = plugin;
        this.parent = parent;
        this.name = name;
        this.file = new File(this.plugin.getDataFolder(), this.name);
        if (!this.file.exists() && this.parent)
            createParent(this.name, this.file);
        if (!this.file.exists() && !this.parent)
            createFile(this.file);
        load(this.file);
    }

    public void reload() throws IOException, InvalidConfigurationException {
        this.file = new File(this.plugin.getDataFolder(), this.name);
        if (!this.file.exists() && this.parent)
            createParent(this.name, this.file);
        if (!this.file.exists() && !this.parent)
            createFile(this.file);
        load(this.file);
    }

    public void save() throws IOException {
        save(this.file);
    }

    private void createParent(String name, File file) throws IOException {
        this.file.getParentFile().mkdir();
        if (this.plugin.getResource(name) != null)
            this.plugin.saveResource(name, false);
        createFile(file);
    }

    private void createFile(File file) throws IOException {
        file.createNewFile();
    }
}
