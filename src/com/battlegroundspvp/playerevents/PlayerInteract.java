package com.battlegroundspvp.playerevents;
/* Created by GamerBah on 8/13/2016 */

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.commands.FreezeCommand;
import com.battlegroundspvp.menus.Crates.CrateMenu;
import com.battlegroundspvp.runnables.UpdateRunnable;
import com.battlegroundspvp.utils.inventories.InventoryBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
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

        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                if (BattlegroundsCore.getInstance().getCrateLocations().contains(event.getClickedBlock().getLocation())) {
                    event.setCancelled(true);
                    new InventoryBuilder(player, new CrateMenu(player, event.getClickedBlock().getLocation())).open();
                }
            }
        }

        if (FreezeCommand.frozen || FreezeCommand.frozenPlayers.contains(player)) {
            event.setCancelled(true);
            return;
        }

        for (BattleModule module : BattleModuleLoader.modules.keySet())
            module.onPlayerInteractItem(event);

        if (item != null) {
            if (UpdateRunnable.updating) {
                event.setCancelled(true);
            }
        }
    }
}
