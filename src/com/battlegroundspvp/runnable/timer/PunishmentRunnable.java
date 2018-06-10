package com.battlegroundspvp.runnable.timer;
/* Created by GamerBah on 8/25/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.punishment.Punishment;
import com.battlegroundspvp.util.enums.EventSound;
import net.md_5.bungee.api.ChatColor;

import java.time.LocalDateTime;

public class PunishmentRunnable implements Runnable {

    private BattlegroundsCore plugin;

    public PunishmentRunnable(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (GameProfile gameProfile : BattlegroundsCore.getGameProfiles()) {
            for (Punishment punishment : gameProfile.getPunishmentData().getPunishments()) {
                if (!punishment.isPardoned()) {
                    LocalDateTime expiration = punishment.getExpiration();
                    Punishment.Type type = punishment.getType();
                    if (!punishment.getType().equals(Punishment.Type.BAN)) {
                        if (expiration.isBefore(LocalDateTime.now())) {
                            punishment.setPardoned(true);
                            if (type.equals(Punishment.Type.MUTE)) {
                                gameProfile.sendMessage(ChatColor.RED + " \nYou are now able to chat again");
                                gameProfile.sendMessage(ChatColor.GRAY + punishment.getReason().getMessage() + "\n ");
                            }
                            EventSound.playSound(gameProfile.getPlayer(), EventSound.ACTION_SUCCESS);
                        }
                    }
                }
            }
        }
    }
}
