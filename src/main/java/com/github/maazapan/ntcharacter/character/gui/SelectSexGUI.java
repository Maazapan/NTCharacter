package com.github.maazapan.ntcharacter.character.gui;

import com.github.maazapan.ntcharacter.NTCharacter;
import com.github.maazapan.ntcharacter.character.Character;
import com.github.maazapan.ntcharacter.character.manager.CharacterManager;
import com.github.maazapan.ntcharacter.character.sex.CharacterSex;
import com.github.maazapan.ntcharacter.manager.gui.InventoryCreator;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SelectSexGUI extends InventoryCreator {

    private final NTCharacter plugin;
    private final Player player;

    private final Character character;

    public SelectSexGUI(Player player, NTCharacter plugin, Character character) {
        super(player, plugin, "select-sex");
        this.character = character;
        this.player = player;
        this.plugin = plugin;
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

            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_LEVER_CLICK, 1, 1);

            if (actions.contains("[BACK]")) {
                this.setTerminated(true);
                new ClanOptionsGUI(player, plugin, character).init().open();
                return;
            }

            for (String action : actions) {
                String sex = action.split(" ")[1];

                this.setTerminated(true);
                character.setCharacterSex(CharacterSex.valueOf(sex));


                characterManager.terminateEditing(player, character);
            }
        }
    }

    @Override
    public InventoryCreator init() {
        this.create();
        return this;
    }
}
