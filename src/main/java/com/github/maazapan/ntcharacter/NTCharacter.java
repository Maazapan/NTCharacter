package com.github.maazapan.ntcharacter;
import com.github.maazapan.ntcharacter.character.listener.CharacterListener;
import com.github.maazapan.ntcharacter.character.manager.CharacterManager;
import com.github.maazapan.ntcharacter.commands.CharacterCommand;
import com.github.maazapan.ntcharacter.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class NTCharacter extends JavaPlugin {

    private CharacterManager characterManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.characterManager = new CharacterManager(this);
        this.saveDefaultConfig();
        this.characterManager.load();

        this.registerListener();
        this.registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        characterManager.save();
    }

    private void registerCommands(){
        getCommand("ntcharacter").setExecutor(new CharacterCommand(this));
        getCommand("ntcharacter").setTabCompleter(new CharacterCommand(this));
    }

    private void registerListener(){
        getServer().getPluginManager().registerEvents(new CharacterListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);


    }

    public String getPrefix(){
        return getConfig().getString("messages.prefix");
    }

    public CharacterManager getCharacterManager() {
        return characterManager;
    }
}
