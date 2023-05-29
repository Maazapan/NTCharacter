package com.github.maazapan.ntcharacter.manager.extension;

import com.github.maazapan.ntcharacter.NTCharacter;
import com.github.maazapan.ntcharacter.character.Character;
import com.github.maazapan.ntcharacter.character.manager.CharacterManager;
import com.github.maazapan.ntcharacter.utils.KatsuUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CharacterExtension extends PlaceholderExpansion {

    private final CharacterManager characterManager;

    public CharacterExtension(NTCharacter plugin) {
        this.characterManager = plugin.getCharacterManager();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ntcharacter";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Maazapan";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.1";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (characterManager.haveCharacter(player.getUniqueId())) {
            Character character = characterManager.getCharacter(player.getUniqueId());

            if(params.equalsIgnoreCase("clan")){
                return KatsuUtils.coloredHex(character.getClan());
            }

            if(params.equalsIgnoreCase("village")){
                return KatsuUtils.formatVillage(character.getVillages().name());
            }

            if(params.equalsIgnoreCase("sex")){
                return KatsuUtils.formatSex(character.getCharacterSex());
            }

            if (params.equalsIgnoreCase("nick")){
                return character.getNick();
            }
        }
        return "&cNo Encontrado";
    }
}
