package com.github.maazapan.ntcharacter.character.loader;

import com.github.maazapan.ntcharacter.NTCharacter;
import com.github.maazapan.ntcharacter.character.Character;
import com.github.maazapan.ntcharacter.character.manager.CharacterManager;
import com.github.maazapan.ntcharacter.character.sex.CharacterSex;
import com.github.maazapan.ntcharacter.character.villages.Villages;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

public class CharacterLoader {

    private final NTCharacter plugin;

    public CharacterLoader(NTCharacter plugin) {
        this.plugin = plugin;
    }

    public void load() {
        try {
            if (!Files.exists(Paths.get(plugin.getDataFolder() + "/data"))) return;
            File[] listFiles = new File(plugin.getDataFolder() + "/data").listFiles();

            if (listFiles == null) return;
            for (File file : listFiles) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                String name = file.getName().replace(".yml", "");
                UUID uuid = UUID.fromString(name);

                Character character = new Character(uuid);
                character.setNick(config.getString("nick"));
                character.setVillages(Villages.valueOf(config.getString("villages")));

                character.setClan(config.getString("clan"));
                character.setResetPoints(config.getInt("clanPoints"));
                character.setCharacterSex(CharacterSex.valueOf(config.getString("sex")));

                plugin.getCharacterManager().addCharacter(uuid, character);
            }
            plugin.getLogger().info("Se han cargado los personajes correctamente!");

        } catch (Exception e) {
            plugin.getLogger().warning("Error while loading characters: " + e.getMessage());
        }
    }

    public void save() {
        try {
            // Create data folder if it doesn't exist.
            if (!Files.exists(Paths.get(plugin.getDataFolder() + "/data"))) {
                Files.createDirectory(Paths.get(plugin.getDataFolder() + "/data"));
            }

            // Delete all files in data folder.
            File[] listFiles = new File(plugin.getDataFolder() + "/data").listFiles();

            if (listFiles != null) {
                Arrays.stream(listFiles).forEach(File::delete);
            }

            CharacterManager characterManager = plugin.getCharacterManager();

            for (UUID uuid : characterManager.getCharacters().keySet()) {
                Character character = characterManager.getCharacter(uuid);

                File file = new File(plugin.getDataFolder() + "/data/" + uuid + ".yml");
                file.createNewFile();

                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

                config.set("nick", character.getNick());
                config.set("villages", character.getVillages().name());
                config.set("clan", character.getClan());

                config.set("clanPoints", character.getResetPoints());
                config.set("sex", character.getCharacterSex().toString());

                config.save(file);
            }
            plugin.getLogger().info("Se han guardado los personajaes correctamente!");

        } catch (Exception e) {
            plugin.getLogger().warning("Error while saving characters: " + e.getMessage());
        }
    }
}
