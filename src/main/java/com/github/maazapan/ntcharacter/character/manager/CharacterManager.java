package com.github.maazapan.ntcharacter.character.manager;

import com.github.maazapan.ntcharacter.NTCharacter;
import com.github.maazapan.ntcharacter.character.Character;
import com.github.maazapan.ntcharacter.character.gui.SelectVillageGUI;
import com.github.maazapan.ntcharacter.character.loader.CharacterLoader;
import com.github.maazapan.ntcharacter.utils.KatsuUtils;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CharacterManager {

    private final Map<UUID, Character> characters;
    private final Map<UUID, String> editors;

    private final NTCharacter plugin;

    public CharacterManager(NTCharacter plugin) {
        this.characters = new HashMap<>();
        this.editors = new HashMap<>();
        this.plugin = plugin;
    }

    public void load() {
        CharacterLoader loader = new CharacterLoader(plugin);
        new Thread(loader::load).start();
    }

    public void save() {
        CharacterLoader loader = new CharacterLoader(plugin);
        new Thread(loader::save).start();
    }

    /**
     * Start editing character
     *
     * @param player Player to start editing.
     */
    public void startEditing(Player player) {
        FileConfiguration config = plugin.getConfig();

        String[] nickTitle = KatsuUtils.coloredHex(config.getString("messages.titles.nick-name")).split(";");
        player.sendTitle(nickTitle[0], nickTitle[1], 10, 2000, 20);

        for (String s : config.getStringList("messages.nick-name")) {
            player.sendMessage(KatsuUtils.coloredHex(s));
        }
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);

        editors.put(player.getUniqueId(), player.getName());
    }

    /**
     * Cancel editing character
     *
     * @param player Player to cancel editing.
     */
    public void cancelEditing(Player player) {
        FileConfiguration config = plugin.getConfig();
        player.sendTitle(" ", " ");

        player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.cancel-editing")));
        player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);

        editors.remove(player.getUniqueId());
    }

    public void terminateEditing(Player player, Character character) {
        FileConfiguration config = plugin.getConfig();

        player.closeInventory();

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        for (String s : config.getStringList("messages.dialogues.fourth")) {
            player.sendMessage(KatsuUtils.coloredHex(s
                    .replaceAll("%clan%", character.getClan())
                    .replaceAll("%nick%", character.getNick())
                    .replaceAll("%village%", character.getVillages().name())));
        }

        String[] title = KatsuUtils.coloredHex(config.getString("messages.titles.terminated-editing")).split(";");
        player.sendTitle(title[0], title[1], 10, 30, 20);

        if (character.getResetPoints() > 0) {
            character.setResetPoints(character.getResetPoints() - 1);
        }
        editors.remove(player.getUniqueId());

        NBTEntity nbtEntity = new NBTEntity(player);

        if(nbtEntity.getPersistentDataContainer().hasTag("select-clan")){
            nbtEntity.getPersistentDataContainer().removeKey("select-clan");
        }
        this.addCharacter(player.getUniqueId(), character);
    }

    /**
     * Save nickname and continue editing character
     *
     * @param player  Player to save nickname.
     * @param message Nickname to save.
     */
    public void saveNickName(Player player, String message) {
        FileConfiguration config = plugin.getConfig();

        if (message.length() > config.getInt("config.max-nick-length")) {
            player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.nick-too-long")));
            return;
        }
        List<String> blacklist = config.getStringList("config.nick-blacklist").stream()
                .map(String::toLowerCase).collect(Collectors.toList());

        if (blacklist.contains(message)) {
            player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.nick-blacklisted")));
            return;
        }
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);

        for (String s : config.getStringList("messages.dialogues.first")) {
            player.sendMessage(KatsuUtils.coloredHex(s.replaceAll("%nick%", message)));
        }

        player.sendTitle(" ", " ");

        editors.put(player.getUniqueId(), message);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            new SelectVillageGUI(player, plugin, message).init().open();
        }, 50);
    }

    public void addEditor(UUID uuid, String nick) {
        editors.put(uuid, nick);
    }

    public void removeEditor(UUID uuid) {
        editors.remove(uuid);
    }

    public boolean isEditing(UUID uuid) {
        return editors.containsKey(uuid);
    }

    public boolean haveCharacter(UUID uuid) {
        return characters.containsKey(uuid);
    }

    public void removeCharacter(UUID uuid) {
        characters.remove(uuid);
    }


    public Character getCharacter(UUID uuid) {
        return characters.get(uuid);
    }

    public void addCharacter(UUID uuid, Character character) {
        characters.put(uuid, character);
    }

    public Map<UUID, Character> getCharacters() {
        return characters;
    }
}
