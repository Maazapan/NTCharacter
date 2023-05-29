package com.github.maazapan.ntcharacter.listener;

import com.github.maazapan.ntcharacter.NTCharacter;
import com.github.maazapan.ntcharacter.character.manager.CharacterManager;
import com.github.maazapan.ntcharacter.manager.gui.InventoryCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;

public class PlayerListener implements Listener {

    private final NTCharacter plugin;

    public PlayerListener(NTCharacter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        CharacterManager characterManager = plugin.getCharacterManager();
        Player player = event.getPlayer();

        if(characterManager.isEditing(player.getUniqueId())){
            characterManager.cancelEditing(player);
        }
    }

    /*
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        CharacterManager characterManager = plugin.getCharacterManager();
        Player player = event.getPlayer();

        if (characterManager.isEditing(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

     */

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();

        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            if (inventoryHolder instanceof InventoryCreator) {
                InventoryCreator inventoryCreator = (InventoryCreator) inventoryHolder;
                inventoryCreator.onClick(event);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();

        if (inventoryHolder instanceof InventoryCreator) {
            InventoryCreator inventoryCreator = (InventoryCreator) inventoryHolder;
            CharacterManager characterManager = plugin.getCharacterManager();

            Player player = (Player) event.getPlayer();

            if (!inventoryCreator.isTerminated()) {
                characterManager.cancelEditing(player);
            }
        }
    }
}
