package com.github.maazapan.ntcharacter.character;

import com.github.maazapan.ntcharacter.character.sex.CharacterSex;
import com.github.maazapan.ntcharacter.character.villages.Villages;

import java.util.UUID;

public class Character {

    private final UUID uuid;
    private String nick;

    private Villages villages;
    private String clan;
    private int resetPoints;

    private CharacterSex characterSex;

    public Character(UUID uuid) {
        this.resetPoints = 1;
        this.uuid = uuid;
    }

    public Villages getVillages() {
        return villages;
    }

    public int getResetPoints() {
        return resetPoints;
    }

    public void setResetPoints(int resetPoints) {
        this.resetPoints = resetPoints;
    }

    public void setVillages(Villages villages) {
        this.villages = villages;
    }

    public String getClan() {
        return clan;
    }

    public void setClan(String clan) {
        this.clan = clan;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }


    public CharacterSex getCharacterSex() {
        return characterSex;
    }

    public void setCharacterSex(CharacterSex characterSex) {
        this.characterSex = characterSex;
    }

    public UUID getUUID() {
        return uuid;
    }
}
