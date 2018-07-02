package com.battlegroundspvp.event;
/* Created by GamerBah on 10/6/2017 */

import com.battlegroundspvp.administration.command.FreezeCommand;
import com.battlegroundspvp.util.UpdateManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamage implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (UpdateManager.isUpdating() || FreezeCommand.reloadFreeze || FreezeCommand.frozen || FreezeCommand.frozenPlayers.contains(player)) {
                if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    e.setCancelled(true);
                    player.setFallDistance(0);
                }
            }
        }
    }
}
