package com.battlegroundspvp.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public enum EventSound {

    ACTION_FAIL(Sound.ITEM_FLINTANDSTEEL_USE, 1, 1, null, 0, 0),
    ACTION_SUCCESS(Sound.BLOCK_NOTE_HARP, 1, 2F, null, 0, 0),

    CLICK(Sound.BLOCK_COMPARATOR_CLICK, 0.4F, 1.5F, null, 0, 0),
    COMMAND_NEEDS_CONFIRMATION(Sound.ENTITY_ENDERMEN_TELEPORT, 1, 1, null, 0, 0),

    TEAM_REQUEST(Sound.ENTITY_ITEM_PICKUP, 2F, 0.3F, null, 0, 0),
    TEAM_REQUEST_DENY(Sound.ENTITY_VILLAGER_NO, 1, 0.75F, null, 0, 0),
    TEAM_REQUEST_ACCEPT(Sound.ENTITY_VILLAGER_YES, 1, 1, Sound.ENTITY_PLAYER_LEVELUP, 1, 1),
    TEAM_DISBAND(Sound.BLOCK_CHEST_CLOSE, 1, 1, Sound.BLOCK_NOTE_BASS, 1, 0.5F),

    ITEM_RECEIVE_EPIC(Sound.ENTITY_SKELETON_DEATH, 0.75F, 0.5F, Sound.ENTITY_FIREWORK_TWINKLE, 1, 0.9F),
    ITEM_RECEIVE_LEGENDARY(Sound.ENTITY_BLAZE_DEATH, 0.75F, 0.6F, Sound.ENTITY_LIGHTNING_THUNDER, 2, 1F),

    INVENTORY_OPEN_MENU(Sound.ITEM_ARMOR_EQUIP_GENERIC, 0.8F, 0.8F, null, 0, 0),
    INVENTORY_OPEN_SUBMENU(Sound.ITEM_ARMOR_EQUIP_LEATHER, 0.8F, 1.5F, null, 0, 0),
    INVENTORY_GO_BACK(Sound.BLOCK_COMPARATOR_CLICK, 1F, 1.1F, null, 0, 0);

    private Sound sound1;
    private float vol1;
    private float ptch1;
    private Sound sound2;
    private float vol2;
    private float ptch2;

    public static void playSound(Player player, EventSound eventSound) {
        player.playSound(player.getLocation(), eventSound.getSound1(), eventSound.getVol1(), eventSound.getPtch1());
        player.playSound(player.getLocation(), eventSound.getSound2(), eventSound.getVol2(), eventSound.getPtch2());
    }
}
