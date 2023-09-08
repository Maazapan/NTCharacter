package com.github.maazapan.ntcharacter.commands;

import com.github.maazapan.ntcharacter.NTCharacter;
import com.github.maazapan.ntcharacter.character.Character;
import com.github.maazapan.ntcharacter.character.manager.CharacterManager;
import com.github.maazapan.ntcharacter.character.villages.Villages;
import com.github.maazapan.ntcharacter.utils.KatsuUtils;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
             - Teleport player to spawn of a village and clan
             */
            case "teleport": {
                if (!sender.hasPermission("ntcharacter.teleport")) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.no-permission")));
                    return true;
                }

                if (!(sender instanceof Player)) {
                    plugin.getLogger().warning("This command can only be executed by a player.");
                    return true;
                }

                Player player = (Player) sender;

                if (!characterManager.haveCharacter(player.getUniqueId())) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.no-village-found")));
                    return true;
                }

                Character character = characterManager.getCharacter(player.getUniqueId());
                FileConfiguration spawnFile = plugin.getSpawnFile();

                String villageArgs = character.getVillages().toString();
                String clanArgs = character.getClan().toUpperCase();

                if (!spawnFile.contains("spawn." + villageArgs + "." + clanArgs)) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.spawn-not-found")));
                    return true;
                }

                double x = spawnFile.getDouble("spawn." + villageArgs + "." + clanArgs + ".x");
                double y = spawnFile.getDouble("spawn." + villageArgs + "." + clanArgs + ".y");
                double z = spawnFile.getDouble("spawn." + villageArgs + "." + clanArgs + ".z");

                float yaw = (float) spawnFile.getDouble("spawn." + villageArgs + "." + clanArgs + ".yaw");
                float pitch = (float) spawnFile.getDouble("spawn." + villageArgs + "." + clanArgs + ".pitch");

                World world = Bukkit.getWorld(spawnFile.getString("spawn." + villageArgs + "." + clanArgs + ".world"));
                Location location = new Location(world, x, y, z, yaw, pitch);

                player.teleport(location);
                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.teleport-successful")));
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
             - Teleport to spawn of a village and clan
             + /character spawn <village> <clan>
             */
            case "spawn": {
                if (!sender.hasPermission("ntcharacter.spawn")) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.no-permission")));
                    return true;
                }
                if (!(sender instanceof Player)) {
                    plugin.getLogger().warning("This command can only be executed by a player.");
                    return true;
                }

                Player player = (Player) sender;

                if (args.length < 2) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.args-missing-spawn")));
                    return true;
                }

                String villageArgs = args[1].toUpperCase();
                String clanArgs = args[2].toUpperCase();

                FileConfiguration spawnFile = plugin.getSpawnFile();

                if (!spawnFile.contains("spawn." + villageArgs + "." + clanArgs)) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.spawn-not-found")));
                    return true;
                }

                double x = spawnFile.getDouble("spawn." + villageArgs + "." + clanArgs + ".x");
                double y = spawnFile.getDouble("spawn." + villageArgs + "." + clanArgs + ".y");
                double z = spawnFile.getDouble("spawn." + villageArgs + "." + clanArgs + ".z");

                float yaw = (float) spawnFile.getDouble("spawn." + villageArgs + "." + clanArgs + ".yaw");
                float pitch = (float) spawnFile.getDouble("spawn." + villageArgs + "." + clanArgs + ".pitch");

                World world = Bukkit.getWorld(spawnFile.getString("spawn." + villageArgs + "." + clanArgs + ".world"));
                Location location = new Location(world, x, y, z, yaw, pitch);

                player.teleport(location);
                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.teleport-successful")));
            }
            break;

            /*
             - Set spawn at character's location
             + /character start
             */
            case "start": {
                if (!sender.hasPermission("ntcharacter.start")) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.no-permission")));
                    return true;
                }

                if (!(sender instanceof Player)) {
                    plugin.getLogger().warning("This command can only be executed by a player.");
                    return true;
                }

                Player player = (Player) sender;
                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.start-successful")));

                FileConfiguration spawnFile = plugin.getSpawnFile();

                spawnFile.set("start.world", player.getWorld().getName());
                spawnFile.set("start.x", player.getLocation().getX());
                spawnFile.set("start.y", player.getLocation().getY());
                spawnFile.set("start.z", player.getLocation().getZ());

                spawnFile.set("start.yaw", player.getLocation().getYaw());
                spawnFile.set("start.pitch", player.getLocation().getPitch());

                plugin.getSpawnFile().save();
            }
            break;


            /*
             - Set spawn at the current location to clan and village spawn
             + /character setspawn <village> <clan>
             */
            case "setspawn": {
                if (!sender.hasPermission("ntcharacter.setspawn")) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.no-permission")));
                    return true;
                }

                if (!(sender instanceof Player)) {
                    plugin.getLogger().warning("This command can only be executed by a player.");
                    return true;
                }

                Player player = (Player) sender;

                if (args.length < 2) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.args-missing-setspawn")));
                    return true;
                }

                String villageArg = args[1].toUpperCase();
                String clanArg = args[2].toUpperCase();

                if (!Villages.existVillage(villageArg)) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.village-not-found")));
                    return true;
                }
                Villages village = Villages.valueOf(villageArg);

                if (!Villages.existClan(village, clanArg)) {
                    sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.clan-not-found")));
                    return true;
                }

                sender.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + config.getString("messages.spawn-set-successful")
                        .replaceAll("%village%", villageArg)
                        .replaceAll("%clan%", clanArg)));

                FileConfiguration spawnFile = plugin.getSpawnFile();

                spawnFile.set("spawn." + villageArg + "." + clanArg + ".world", player.getWorld().getName());
                spawnFile.set("spawn." + villageArg + "." + clanArg + ".x", player.getLocation().getX());
                spawnFile.set("spawn." + villageArg + "." + clanArg + ".y", player.getLocation().getY());
                spawnFile.set("spawn." + villageArg + "." + clanArg + ".z", player.getLocation().getZ());

                spawnFile.set("spawn." + villageArg + "." + clanArg + ".yaw", player.getLocation().getYaw());
                spawnFile.set("spawn." + villageArg + "." + clanArg + ".pitch", player.getLocation().getPitch());

                plugin.getSpawnFile().save();
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
        List<String> commands = Arrays.asList("reload", "create", "reset", "add", "setspawn", "spawn", "teleport", "start");

        if (args.length == 1) return commands;
        if (args.length > 1) {

            /*
             - Teleport a player to clan and village spawn
             */
            if (args[0].equalsIgnoreCase("spawn")) {
                FileConfiguration spawnFile = plugin.getSpawnFile();

                if (args.length == 2) {
                    return new ArrayList<>(spawnFile.getConfigurationSection("spawn").getKeys(false));
                }

                if (args.length == 3 && spawnFile.contains("spawn." + args[1])) {
                    return new ArrayList<>(spawnFile.getConfigurationSection("spawn." + args[1]).getKeys(false));
                }
            }

            /*
             - Set spawn at the current location to clan and village spawn
             */
            if (args[0].equalsIgnoreCase("setspawn")) {
                if (args.length == 2) {
                    return Arrays.stream(Villages.values())
                            .map(Enum::name)
                            .collect(Collectors.toList());
                }

                if (Villages.existVillage(args[1])) {
                    Villages villages = Villages.valueOf(args[1]);

                    if (args.length == 3) {
                        return Stream.concat(Arrays.stream(villages.getSurnames()), Arrays.stream(Villages.GLOBAL.getSurnames()))
                                .map(String::toUpperCase)
                                .collect(Collectors.toList());
                    }
                }
            }
        }
        return null;
    }
}
