package com.battlegroundspvp.playerevents;
/* Created by GamerBah on 9/22/2017 */

import com.battlegroundspvp.commands.ReportCommand;
import com.battlegroundspvp.utils.inventories.InventoryBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class PlayerCloseInventory implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        if (InventoryBuilder.getInventoryUsers().keySet().contains(player))
            InventoryBuilder.getInventoryUsers().remove(player);

        if (inventory.getName().contains("Reporting:")) {
            if (ReportCommand.getReportBuilders().containsKey(player.getUniqueId()))
                ReportCommand.getReportBuilders().remove(player.getUniqueId());
            if (ReportCommand.getReportArray().containsKey(player.getUniqueId()))
                ReportCommand.getReportArray().remove(player.getUniqueId());
        }
    }
}
