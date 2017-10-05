package com.battlegroundspvp.runnables;
/* Created by GamerBah on 8/25/2016 */

import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.Core;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PunishmentRunnable implements Runnable {

    private Core plugin;

    public PunishmentRunnable(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (plugin.getPlayerPunishments().containsKey(player.getUniqueId())) {
                ArrayList<Punishment> punishments = plugin.getPlayerPunishments().get(player.getUniqueId());
                for (int i = 0; i < punishments.size(); i++) {
                    LocalDateTime date = punishments.get(i).getDate();
                    LocalDateTime expiration = punishments.get(i).getExpiration();
                    Punishment.Type type = punishments.get(i).getType();
                    if (!punishments.get(i).isPardoned()) {
                        if (!punishments.get(i).getType().equals(Punishment.Type.BAN)) {
                            if (expiration.isBefore(LocalDateTime.now())) {
                                //Core.getSql().executeUpdate(Query.UPDATE_PUNISHMENT_PARDONED, true, player.getUniqueId().toString(), type.toString(), date.toString());
                                punishments.get(i).setPardoned(true);
                                if (type.equals(Punishment.Type.MUTE)) {
                                    player.sendMessage(ChatColor.RED + " \nYou are now able to chat again");
                                    player.sendMessage(ChatColor.GRAY + punishments.get(i).getReason().getMessage() + "\n ");
                                }
                                EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                            }
                        }
                    }
                }
            }
        }
    }
}
