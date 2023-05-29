package com.github.maazapan.ntcharacter.character.gui;

import com.github.maazapan.ntcharacter.NTCharacter;
import com.github.maazapan.ntcharacter.character.Character;
import com.github.maazapan.ntcharacter.character.manager.CharacterManager;
import com.github.maazapan.ntcharacter.character.villages.Villages;
import com.github.maazapan.ntcharacter.manager.gui.InventoryCreator;
import com.github.maazapan.ntcharacter.utils.KatsuUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SelectVillageGUI extends InventoryCreator {

    private final NTCharacter plugin;
    private final Player player;

    private String nickName;

    public SelectVillageGUI(Player player, NTCharacter plugin, String nickName) {
        super(player, plugin, "select-village");
        this.nickName = nickName;
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        CharacterManager characterManager = plugin.getCharacterManager();
        FileConfiguration config = plugin.getConfig();

        ItemStack itemStack = event.getCurrentItem();
        NBTItem nbtItem = new NBTItem(itemStack);

        event.setCancelled(true);

        if (nbtItem.hasTag("character-actions")) {
            List<String> actions = nbtItem.getObject("character-actions", List.class);

            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);

            if (actions.contains("[CLOSE]")) {
                player.closeInventory();
                characterManager.cancelEditing(player);
                return;
            }

            for (String action : actions) {
                if (action.startsWith("[VILLAGE]")) {

                    Character character = new Character(player.getUniqueId());
                    character.setVillages(Villages.valueOf(action.split(" ")[1]));
                    character.setNick(nickName);

                    this.setTerminated(true);

                    for (String s : config.getStringList("messages.dialogues.second")) {
                        player.sendMessage(KatsuUtils.coloredHex(s
                                .replaceAll("%village%", KatsuUtils.formatVillage(character.getVillages().name()))
                                .replaceAll("%nick%", character.getNick())));
                    }

                    String[] title = KatsuUtils.coloredHex(config.getString("messages.titles.select-village")
                            .replaceAll("%village%", KatsuUtils.formatVillage(character.getVillages().name()))).split(";");

                    player.sendTitle(title[0], title[1], 10, 30, 20);
                    player.closeInventory();

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        new ClanOptionsGUI(player, plugin, character).init().open();
                    }, 50);
                }
            }
        }
    }

    @Override
    public InventoryCreator init() {
        this.create();
        return this;
    }
}
