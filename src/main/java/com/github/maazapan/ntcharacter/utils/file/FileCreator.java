package com.github.maazapan.ntcharacter.utils.file;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class FileCreator extends YamlConfiguration {

    private final String name;
    private final String path;

    private final Plugin plugin;
    private File file;

    public FileCreator(String name, String path, Plugin plugin) {
        this.name = name;
        this.plugin = plugin;
        this.path = path;
    }

    public FileCreator create() {
        file = new File(path, name);

        if (!file.exists()) {
            this.saveDefault();
        }
        this.reload();
        return this;
    }

    public void reload() {
        try {
            super.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            super.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File toFile() {
        return file;
    }

    public void saveDefault() {
        plugin.saveResource(name, false);
    }
}
