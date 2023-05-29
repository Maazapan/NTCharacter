package com.github.maazapan.ntcharacter.character.villages;

import java.util.HashMap;
import java.util.Map;

public enum Villages {

    AMEGAKURE("Fuma", "Tifo", "Tanaka"),
    KONOHAGAKURE("Hyuga", "Uchiha", "Nara"),
    SUNAGAKURE("Sabaku", "Shaku", "Shirogane"),
    IWAGAKURE("Kazan", "Beifong", "Bakuhatsu"),
    KUMOGAKURE("Chinoike", "Burakkurei", "Yotsuki"),
    KIRIGAKURE("Yuki", "Hozuki", "Hoshiraki"),
    GLOBAL("Uzumaki", "Sarutobi", "Kaguya");

    private final String[] surnames;

    Villages(String... surnames) {
        this.surnames = surnames;
    }

    public String[] getSurnames() {
        return surnames;
    }
}
