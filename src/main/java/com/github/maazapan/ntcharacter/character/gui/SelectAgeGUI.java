package com.github.maazapan.ntcharacter.character.gui;

import com.github.maazapan.ntcharacter.NTCharacter;
import com.github.maazapan.ntcharacter.character.Character;
import com.github.maazapan.ntcharacter.character.manager.CharacterManager;
import com.github.maazapan.ntcharacter.manager.gui.InventoryCreator;
import com.github.maazapan.ntcharacter.utils.item.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SelectAgeGUI extends InventoryCreator {

    private final Player player;
    private final NTCharacter plugin;

    private final Character character;

    private int age = 20;

    public SelectAgeGUI(Player player, NTCharacter plugin, Character character) {
        super(player, plugin, "select-age");
        this.character = character;
        this.player = player;
        this.plugin = plugin;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        NBTItem nbtItem = new NBTItem(itemStack);

        CharacterManager characterManager = plugin.getCharacterManager();

        event.setCancelled(true);

        if (nbtItem.hasTag("character-actions")) {
            List<String> actions = nbtItem.getObject("character-actions", List.class);

            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);

            if (actions.contains("[BACK]")) {
                this.setTerminated(true);
                new SelectSexGUI(player, plugin, character).init().open();
                return;
            }

            for (String action : actions) {
                if (action.startsWith("[CONFIRM]")) {
                    this.setTerminated(true);

                    character.setAge(age);
                    characterManager.terminateEditing(player, character);
                    return;
                }

                if (action.startsWith("[ADD]")) {
                    if ((age + 1) <= 100) {
                        this.setTerminated(true);

                        age++;
                        init().open();
                        this.setTerminated(false);
                        return;
                    }
                }

                if (action.startsWith("[REMOVE]")) {
                    if ((age - 1) >= 20) {
                        this.setTerminated(true);

                        age--;
                        init().open();
                        this.setTerminated(false);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public InventoryCreator init() {
        this.create();
        //  this.setTerminated(false);

        FileConfiguration config = plugin.getConfig();

        ItemStack itemStack = new ItemBuilder()
                .fromConfig(config, "inventory.select-age.age-item")
                .replace("%age%", String.valueOf(age))
                .build();

        getInventory().setItem(13, itemStack);
        return this;
    }
}
