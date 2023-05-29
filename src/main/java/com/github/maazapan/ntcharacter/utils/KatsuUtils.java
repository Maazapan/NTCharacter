package com.github.maazapan.ntcharacter.utils;

import com.github.maazapan.ntcharacter.character.sex.CharacterSex;
import com.github.maazapan.ntcharacter.character.villages.Villages;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KatsuUtils {
    public static String serialize(ItemStack itemStack) {
        try {
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            BukkitObjectOutputStream objectOutput = new BukkitObjectOutputStream(byteArray);

            objectOutput.writeObject(itemStack);
            objectOutput.flush();

            return Base64.getEncoder().encodeToString(byteArray.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack deserialize(String base64) {
        try {
            ByteArrayInputStream byteArray = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
            BukkitObjectInputStream objectInput = new BukkitObjectInputStream(byteArray);

            return (ItemStack) objectInput.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatVillage(String villages) {
        Map<String, String> villagesMap = new HashMap<>() {
            {
                put("AMEGAKURE", "&d&lAmegakure");
                put("KONOHAGAKURE", "&a&lKonohagakure");
                put("SUNAGAKURE", "&6&lSunagakure");
                put("IWAGAKURE", "#A24700&lIwagakure");
                put("KUMOGAKURE", "&e&lKumogakure");
                put("KIRIGAKURE", "&b&lKirigakure");
            }
        };

        if (!villagesMap.containsKey(villages)) {
            return "&7" + villages.substring(0, 1).toUpperCase() + villages.substring(1).toLowerCase();
        }
        return villagesMap.get(villages);
    }

    public static String formatVillage(Villages villages) {
        return formatVillage(villages.toString());
    }

    public static String formatSex(CharacterSex characterSex) {

        switch (characterSex) {
            case MALE:
                return "&b&lHombre";
            case FEMALE:
                return "&d&lMujer";
            case OTHER:
                return "&7&lOtro";
        }
        return "&7&lOtro";
    }

    public static String formatClan(String clan) {
        clan = clan.toUpperCase();
        Map<String, String> clanMap = new HashMap<>() {
            {
                put("HYUGA", "&f&lHyuga");
                put("UCHIHA", "&c&lUchiha");
                put("NARA", "&8&lNara");

                put("SABAKU", "&6&lSabaku");
                put("SHIROGANE", "&d&lShirogane");
                put("SHAKU", "#FEBC17&lShaku");

                put("BEIFON", "&8&lBeifon");
                put("KAZAN", "&6&lKazan");
                put("BAKUHATSU", "&f&lBakuhatsu");
                put("YUKI", "&f&lYuki");
                put("HOZUKI", "&9&lHozuki");
                put("HOSHIRAKI", "&b&lHoshiraki");
                put("FUMA", "&d&lFuma");
                put("TIFO", "&3&lTifo");
                put("YOTSUKI", "&b&lYotsuki");
                put("BURAKKUREI", "&5&lBurakkurei");
                put("CHINOIKE", "&c&lChinoike");
            }
        };

        if (!clanMap.containsKey(clan)) {
            return "&7" + clan.substring(0, 1).toUpperCase() + clan.substring(1).toLowerCase();
        }
        return clanMap.get(clan);
    }

    public static String formatTime(long time) {
        return TimeUnit.MILLISECONDS.toMinutes(time) + ":" + TimeUnit.MILLISECONDS.toSeconds(time) % 60;
    }

    public static Location center(Location location) {
        return location.add(0.5, 0, 0.5);
    }

    public static String coloredHex(String message) {
        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String hexCode = message.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');

            char[] ch = replaceSharp.toCharArray();
            StringBuilder builder = new StringBuilder("");
            for (char c : ch) {
                builder.append("&" + c);
            }
            message = message.replace(hexCode, builder.toString());
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}

