package com.github.maazapan.ntcharacter.manager.gui;

import com.github.maazapan.ntcharacter.utils.KatsuUtils;
import com.github.maazapan.ntcharacter.utils.item.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public abstract class InventoryCreator implements InventoryHolder {

    private Inventory inventory;
    private String path;

    private Player player;
    private Plugin plugin;

    private boolean terminated = false;

    public InventoryCreator(Player player, Plugin plugin, String path) {
        this.plugin = plugin;
        this.player = player;
        this.path = "inventory." + path;
    }

    protected InventoryCreator create() {
        FileConfiguration config = plugin.getConfig();
        try {

            if (config.isSet(path + ".inventory_type")) {
                InventoryType inventoryType = InventoryType.valueOf(config.getString(path + ".inventory_type"));
                this.inventory = Bukkit.createInventory(this, inventoryType, KatsuUtils.coloredHex(config.getString(path + ".title")));

            } else {
                this.inventory = Bukkit.createInventory(this, config.getInt(path + ".size"), KatsuUtils.coloredHex(config.getString(path + ".title")));
            }

            for (String key : config.getConfigurationSection(path + ".items").getKeys(false)) {
                ItemBuilder itemBuilder = new ItemBuilder(Material.valueOf(config.getString(path + ".items." + key + ".id")));

                if (config.isSet(path + ".items." + key + ".texture")) {
                    itemBuilder.setSkullBase64(config.getString(path + ".items." + key + ".texture"));
                }

                if (config.isSet(path + ".items." + key + ".owner")) {
                    itemBuilder.setSkullOwner(config.getString(path + ".items." + key + ".owner"));
                }

                if (config.isSet(path + ".items." + key + ".display_name")) {
                    itemBuilder.setName(config.getString(path + ".items." + key + ".display_name"));
                }

                if (config.isSet(path + ".items." + key + ".lore")) {
                    itemBuilder.setLore(config.getStringList(path + ".items." + key + ".lore"));
                }

                if (config.isSet(path + ".items." + key + ".custom_model_data")) {
                    itemBuilder.setModelData(config.getInt(path + ".items." + key + ".custom_model_data"));
                }

                if(config.isSet(path + ".items." + key + ".item_flags")){
                    List<ItemFlag> itemFlags = config.getStringList(path + ".items." + key + ".item_flags")
                            .stream().map(ItemFlag::valueOf).collect(Collectors.toList());

                    for (ItemFlag itemFlag : itemFlags) {
                        itemBuilder.addFlag(itemFlag);
                    }
                }

                NBTItem nbtItem = new NBTItem(itemBuilder.build());

                if (config.isSet(path + ".items." + key + ".actions")) {
                    nbtItem.setObject("character-actions", config.getStringList(path + ".items." + key + ".actions"));

                } else {
                    nbtItem.setString("character-actions-item", "actions");
                }
                nbtItem.applyNBT(itemBuilder.build());

                if (config.isSet(path + ".items." + key + ".slots")) {
                    for (Integer slots : config.getIntegerList(path + ".items." + key + ".slots")) {
                        inventory.setItem(slots, itemBuilder.build());
                    }
                } else {
                    inventory.setItem(config.getInt(path + ".items." + key + ".slot"), itemBuilder.build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public abstract void onClick(InventoryClickEvent event);

    public abstract InventoryCreator init();

    public void open() {
        player.openInventory(inventory);
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
