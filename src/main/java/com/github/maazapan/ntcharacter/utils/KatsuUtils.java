package com.github.maazapan.ntcharacter.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
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

