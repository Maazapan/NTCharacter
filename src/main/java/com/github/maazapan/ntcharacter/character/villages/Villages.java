package com.github.maazapan.ntcharacter.character.villages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Villages {

    AMEGAKURE("Fuma", "Tifo", "Tanaka"),
    KONOHAGAKURE("Hyuga", "Uchiha", "Nara"),
    SUNAGAKURE("Sabaku", "Shaku", "Shirogane"),
    IWAGAKURE("Kazan", "Beifong", "Bakuhatsu"),
    KUMOGAKURE("Chinoike", "Burakkurei", "Yotsuki"),
    KIRIGAKURE("Yuki", "Hozuki", "Hoshiraki"),
    GLOBAL("Uzumaki", "Sarutobi", "Kaguya", "Taijutsu");

    private final String[] surnames;

    Villages(String... surnames) {
        this.surnames = surnames;
    }

    public String[] getSurnames() {
        return surnames;
    }

    public static boolean existClan(Villages villages, String clan) {
        List<String> clans = new ArrayList<>(List.of(villages.getSurnames()));
        clans.addAll(List.of(GLOBAL.getSurnames()));

      return clans.stream().anyMatch(c -> c.equalsIgnoreCase(clan));
    }

    public static boolean existVillage(String village){
        return Arrays.stream(values()).anyMatch(v -> v.name().equalsIgnoreCase(village));
    }
}
