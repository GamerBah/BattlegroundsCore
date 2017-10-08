package com.battlegroundspvp.playerevents;
/* Created by GamerBah on 8/13/2016 */

import com.battlegroundspvp.administration.commands.FreezeCommand;
import com.battlegroundspvp.runnables.UpdateRunnable;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!player.getGameMode().equals(GameMode.CREATIVE) && !(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                || event.getAction().equals(Action.RIGHT_CLICK_AIR))) {
            event.setCancelled(true);
        }

        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL) {
            event.setCancelled(true);
        }

        if (FreezeCommand.frozen || FreezeCommand.frozenPlayers.contains(player)) {
            event.setCancelled(true);
            return;
        }

        if (item != null) {
            if (UpdateRunnable.updating) {
                event.setCancelled(true);
                return;
            }
            if (item.getType().equals(Material.POISONOUS_POTATO) || item.getType().equals(Material.POTATO_ITEM)) {
                event.setCancelled(true);
            }
        }
    }
}
