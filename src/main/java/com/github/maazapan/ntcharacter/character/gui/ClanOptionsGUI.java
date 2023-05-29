package com.github.maazapan.ntcharacter.character.gui;

import com.github.maazapan.ntcharacter.NTCharacter;
import com.github.maazapan.ntcharacter.character.Character;
import com.github.maazapan.ntcharacter.character.villages.Villages;
import com.github.maazapan.ntcharacter.manager.gui.InventoryCreator;
import com.github.maazapan.ntcharacter.utils.KatsuUtils;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ClanOptionsGUI extends InventoryCreator {

    private final NTCharacter plugin;
    private final Player player;

    private final Character character;

    public ClanOptionsGUI(Player player, NTCharacter plugin, Character character) {
        super(player, plugin, "options-clan");
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
                new SelectVillageGUI(player, plugin, character.getNick()).init().open();
                return;
            }

            for (String action : actions) {
                switch (action.toUpperCase()) {
                    case "[RANDOM-CLAN]": {
                        List<String> availableClans = Arrays.stream(character.getVillages().getSurnames())
                                .collect(Collectors.toList());
                        availableClans.addAll(Arrays.asList(Villages.GLOBAL.getSurnames()));
                        String clan = availableClans.get(new Random().nextInt(availableClans.size()));

                        character.setClan(clan);
                        this.setTerminated(true);

                        for (String s : config.getStringList("messages.dialogues.third")) {
                            player.sendMessage(KatsuUtils.coloredHex(s
                                    .replaceAll("%clan%", clan)
                                    .replaceAll("%village%", character.getVillages().name())));
                        }
                        player.closeInventory();

                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            new SelectSexGUI(player, plugin, character).init().open();
                        }, 40);
                    }
                    break;

                    case "[SELECT-CLAN]": {
                        NBTEntity nbtEntity = new NBTEntity(player);

                        if (!nbtEntity.getPersistentDataContainer().hasTag("select-clan")) {
                            player.playSound(player.getLocation(), Sound.BLOCK_CHAIN_BREAK, 1, 0.5f);
                            player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.blocked-select-clan")));
                            return;
                        }

                        this.setTerminated(true);
                        new SelectClanGUI(player, plugin, character).init().open();
                    }
                    break;

                    case "[TAIJUTSU]": {
                        character.setClan("Taijutsu");
                        this.setTerminated(true);

                        for (String s : config.getStringList("messages.dialogues.third")) {
                            player.sendMessage(KatsuUtils.coloredHex(s
                                    .replaceAll("%clan%", "Taijutsu")
                                    .replaceAll("%village%", character.getVillages().name())));
                        }

                        player.closeInventory();

                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            new SelectSexGUI(player, plugin, character).init().open();
                        }, 40);
                    }
                    break;
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
