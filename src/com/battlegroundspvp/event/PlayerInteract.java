package com.battlegroundspvp.event;
/* Created by GamerBah on 8/13/2016 */

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.administration.command.FreezeCommand;
import com.battlegroundspvp.gui.cosmetic.CrateMenu;
import com.battlegroundspvp.util.BattleCrate;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.manager.BattleCrateManager;
import com.battlegroundspvp.util.manager.UpdateManager;
import com.gamerbah.inventorytoolkit.InventoryBuilder;
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
                    for (BattleCrate battleCrate : BattleCrateManager.getCrates()) {
                        if (battleCrate.getLocation().hashCode() == event.getClickedBlock().getLocation().hashCode()) {
                            event.setCancelled(true);
                            if (UpdateManager.isUpdating()) {
                                return;
                            }
                            if (!BattleCrateManager.isUsing(player)) {
                                new InventoryBuilder(player, new CrateMenu(player, event.getClickedBlock().getLocation())).open();
                            } else {
                                EventSound.playSound(player, EventSound.ACTION_FAIL);
                                player.sendMessage(ChatColor.RED + "Wait for this Battle Crate to finish opening!");
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
            if (UpdateManager.isUpdating()) {
                event.setCancelled(true);
            }
        }
    }
}
