package com.battlegroundspvp.event;
/* Created by GamerBah on 9/22/2017 */

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.command.ReportCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class PlayerCloseInventory implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        for (BattleModule module : BattleModuleLoader.modules.keySet())
            module.onPlayerCloseInventory(event);

        //if (InventoryBuilder.getInventoryUsers().keySet().contains(player))
        //    InventoryBuilder.getInventoryUsers().remove(player);

        if (inventory.getName().contains("Reporting:")) {
            ReportCommand.getReportBuilders().remove(player.getUniqueId());
            ReportCommand.getReportArray().remove(player.getUniqueId());
        }
    }
}
