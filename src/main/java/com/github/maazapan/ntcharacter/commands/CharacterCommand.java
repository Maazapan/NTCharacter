package com.github.maazapan.ntcharacter.commands;

import com.github.maazapan.ntcharacter.NTCharacter;
import com.github.maazapan.ntcharacter.character.Character;
import com.github.maazapan.ntcharacter.character.manager.CharacterManager;
import com.github.maazapan.ntcharacter.utils.KatsuUtils;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CharacterCommand implements CommandExecutor, TabCompleter {

    private final NTCharacter plugin;

    public CharacterCommand(NTCharacter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        CharacterManager characterManager = plugin.getCharacterManager();
        FileConfiguration config = plugin.getConfig();
    //    Player player = (Player) sender;

        if (!(args.length > 0)) {
            sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.args-missing")));
            return true;
        }

        switch (args[0].toLowerCase()) {

            /*
             - Reload plugin configuration
             + /character reload
             */
            case "reload": {
                if (!sender.hasPermission("ntcharacter.reload")) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.no-permission")));
                    return true;
                }
                plugin.reloadConfig();
                plugin.saveDefaultConfig();

                sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&aPlugin recargado correctamente."));
            }
            break;

            /*
             - Reset character of player
             + /character reset <player> or /character reset
             */
            case "reset": {
                if (args.length > 1) {
                    String targetName = args[1];

                    if (!sender.hasPermission("ntcharacter.reset")) {
                        sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.no-permission")));
                        return true;
                    }

                    if (Bukkit.getPlayer(targetName) == null) {
                        sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.player-not-found")));
                        return true;
                    }
                    Player target = Bukkit.getPlayer(targetName);

                    characterManager.startEditing(target);
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.reset-successful").replaceAll("%player%", target.getName())));
                    return true;
                }

                if (!(sender instanceof Player)) {
                    plugin.getLogger().warning("This command can only be executed by a player. or use /ntc reset <player>");
                    return true;
                }
                Player player = (Player) sender;

                if (characterManager.haveCharacter(player.getUniqueId())) {
                    Character character = characterManager.getCharacter(player.getUniqueId());

                    if (character.getResetPoints() <= 0) {
                        player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.no-reset-points")));
                        return true;
                    }
                }
                characterManager.startEditing(player);
            }
            break;

            /*
             - Create a new character if the player doesn't have one
             + /character create
             */
            case "create": {
                if (!(sender instanceof Player)) {
                    plugin.getLogger().warning("This command can only be executed by a player.");
                    return true;
                }

                Player player = (Player) sender;

                if (!player.hasPermission("ntcharacter.create")) {
                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.no-permission")));
                    return true;
                }

                if (characterManager.isEditing(player.getUniqueId())) {
                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.already-editing")));
                    return true;
                }

                if (characterManager.haveCharacter(player.getUniqueId())) {
                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.already-have-character")));
                    return true;
                }
                characterManager.startEditing((Player) sender);
            }
            break;

            /*
             - Add reset points to a player
                + /character add <player> <amount>
             */
            case "add": {
                if (!sender.hasPermission("ntcharacter.add")) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.no-permission")));
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.args-missing-add")));
                    return true;
                }

                if (Bukkit.getPlayer(args[1]) == null) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.sender-not-found")));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.select-successful").replaceAll("%player%", target.getName())));

                NBTEntity nbtEntity = new NBTEntity(target);
                nbtEntity.getPersistentDataContainer().setString("select-clan", sender.getName());
            }
            break;

            default:
                sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.args-missing")));
                break;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("ntcharacter.admin")) {
            return null;
        }

        if (args.length == 1) {
            return Arrays.asList("reload", "create", "reset", "add");
        }
        return null;
    }
}
