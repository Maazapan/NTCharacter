package com.github.maazapan.ntcharacter.character.gui;

import com.github.maazapan.ntcharacter.NTCharacter;
import com.github.maazapan.ntcharacter.character.Character;
import com.github.maazapan.ntcharacter.manager.gui.InventoryCreator;
import com.github.maazapan.ntcharacter.utils.KatsuUtils;
import com.github.maazapan.ntcharacter.utils.item.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SelectClanGUI extends InventoryCreator {

    private final NTCharacter plugin;
    private final Player player;

    private final Character character;

    public SelectClanGUI(Player player, NTCharacter plugin, Character character) {
        super(player, plugin, "select-clan");
        this.character = character;
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        FileConfiguration config = plugin.getConfig();

        ItemStack itemStack = event.getCurrentItem();
        NBTItem nbtItem = new NBTItem(itemStack);

        event.setCancelled(true);

        if (nbtItem.hasTag("character-actions")) {
            List<String> actions = nbtItem.getObject("character-actions", List.class);

            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);

            if (actions.contains("[BACK]")) {
                this.setTerminated(true);
                new ClanOptionsGUI(player, plugin, character).init().open();
                return;
            }

            for (String action : actions) {
                if(action.startsWith("[CLAN]")) {
                    String clan = action.split(" ")[1];
                    character.setClan(clan);

                    this.setTerminated(true);

                    for (String s : config.getStringList("messages.dialogues.third")) {
                        player.sendMessage(KatsuUtils.coloredHex(s
                                .replaceAll("%clan%", KatsuUtils.formatClan(clan, plugin))
                                .replaceAll("%village%", KatsuUtils.formatVillage(character.getVillages().name(), plugin))));
                    }

                    String[] title = KatsuUtils.coloredHex(config.getString("messages.titles.select-clan")
                            .replaceAll("%clan%", KatsuUtils.formatClan(clan, plugin))).split(";");

                    player.sendTitle(title[0], title[1], 10, 30, 20);
                    player.closeInventory();

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        new SelectSexGUI(player, plugin, character).init().open();
                    }, 40);
                }
            }
        }
    }

    @Override
    public InventoryCreator init() {
        this.create();

        final FileConfiguration config = plugin.getConfig();
        final List<ItemStack> itemStacks = new ArrayList<>();

        for (String key : config.getConfigurationSection("inventory.select-clan.clan-items." + character.getVillages()).getKeys(false)) {
            itemStacks.add(new ItemBuilder().fromConfig(config, "inventory.select-clan.clan-items." + character.getVillages() + "." + key).build());
        }

        int i = 10;
        for (ItemStack itemStack : itemStacks) {
            getInventory().setItem(i, itemStack);
            i++;
        }
        return this;
    }
}
