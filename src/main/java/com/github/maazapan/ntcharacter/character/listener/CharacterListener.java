package com.github.maazapan.ntcharacter.character.listener;

import com.github.maazapan.ntcharacter.NTCharacter;
import com.github.maazapan.ntcharacter.character.manager.CharacterManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class CharacterListener implements Listener {

    private final NTCharacter plugin;

    public CharacterListener(NTCharacter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) {
        CharacterManager characterManager = plugin.getCharacterManager();
        String message = event.getMessage();
        Player player = event.getPlayer();

        if (characterManager.isEditing(player.getUniqueId())) {
            event.setCancelled(true);

            if (message.startsWith("cancel")  || message.startsWith("cancelar")) {
                characterManager.cancelEditing(player);
                return;
            }
            characterManager.saveNickName(player, message);
        }
    }
}
