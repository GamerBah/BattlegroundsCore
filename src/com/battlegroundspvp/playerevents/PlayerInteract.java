package com.battlegroundspvp.playerevents;
/* Created by GamerBah on 8/13/2016 */

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.commands.FreezeCommand;
import com.battlegroundspvp.administration.donations.CrateItem;
import com.battlegroundspvp.menus.crates.CrateMenu;
import com.battlegroundspvp.runnables.UpdateRunnable;
import com.battlegroundspvp.utils.Crate;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.inventories.InventoryBuilder;
import net.md_5.bungee.api.ChatColor;
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
                if (player.getGameMode() != GameMode.CREATIVE) {
                    for (Crate crate : BattlegroundsCore.getCrates()) {
                        if (crate.getLocation().hashCode() == event.getClickedBlock().getLocation().hashCode()) {
                            event.setCancelled(true);
                            if (UpdateRunnable.updating) {
                                return;
                            }
                            if (!CrateItem.isOpening(player)) {
                                new InventoryBuilder(player, new CrateMenu(player, event.getClickedBlock().getLocation())).open();
                            } else {
                                EventSound.playSound(player, EventSound.ACTION_FAIL);
                                player.sendMessage(ChatColor.RED + "Wait for this crate to finish opening!");
                                return;
                            }
                        }
                    }
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
