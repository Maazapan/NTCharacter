package com.github.maazapan.ntcharacter;

import com.github.maazapan.ntcharacter.character.listener.CharacterListener;
import com.github.maazapan.ntcharacter.character.manager.CharacterManager;
import com.github.maazapan.ntcharacter.commands.CharacterCommand;
import com.github.maazapan.ntcharacter.listener.PlayerListener;
import com.github.maazapan.ntcharacter.manager.extension.CharacterExtension;
import com.github.maazapan.ntcharacter.utils.file.FileCreator;
import org.bukkit.plugin.java.JavaPlugin;

public final class NTCharacter extends JavaPlugin {

    private CharacterManager characterManager;
    private FileCreator spawnFile;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.characterManager = new CharacterManager(this);
        this.registerConfig();
        this.characterManager.load();

        this.registerListener();
        this.registerCommands();
        this.registerPapi();
    }

    private void registerConfig() {
        spawnFile = new FileCreator("spawn.yml", getDataFolder().getPath(), this).create();
        this.saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        characterManager.save();
    }

    private void registerCommands() {
        getCommand("ntcharacter").setExecutor(new CharacterCommand(this));
        getCommand("ntcharacter").setTabCompleter(new CharacterCommand(this));
    }

    private void registerListener(){
        getServer().getPluginManager().registerEvents(new CharacterListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    private void registerPapi(){
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            System.out.println("NTCharacter: PlaceholderAPI found, registering placeholders");
            CharacterExtension characterExtension = new CharacterExtension(this);
            characterExtension.register();
        }
    }

    public String getPrefix(){
        return getConfig().getString("messages.prefix");
    }

    public FileCreator getSpawnFile() {
        return spawnFile;
    }

    public CharacterManager getCharacterManager() {
        return characterManager;
    }
}
