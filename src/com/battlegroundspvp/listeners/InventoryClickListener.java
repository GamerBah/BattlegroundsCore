package com.battlegroundspvp.listeners;
/* Created by GamerBah on 8/4/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.inventories.ClickEvent;
import com.battlegroundspvp.utils.inventories.GameInventory;
import com.battlegroundspvp.utils.inventories.InventoryBuilder;
import com.battlegroundspvp.utils.inventories.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    private BattlegroundsCore plugin = BattlegroundsCore.getInstance();

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.isCancelled()) {
            Inventory inventory = event.getInventory();
            final Player player = (Player) event.getWhoClicked();

            if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                if (event.getSlotType() == null || event.getCurrentItem() == null
                        || event.getCurrentItem().getType() == null || event.getCurrentItem().getItemMeta() == null) {
                    return;
                }

                if (inventory == player.getInventory())
                    event.setCancelled(true);

                for (ItemStack itemStack : player.getInventory().getArmorContents()) {
                    if (itemStack == null) {
                        event.setCancelled(true);
                    }
                    if (event.getCurrentItem().equals(itemStack)) {
                        event.setCancelled(true);
                    }
                }

                if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null
                        && event.getCurrentItem().getItemMeta().getDisplayName() != null) {

                    if (InventoryBuilder.getInventoryUsers().containsKey(player)) {
                        ItemBuilder itemBuilder = null;
                        GameInventory gameInventory = InventoryBuilder.getInventoryUsers().get(player).getGameInventory();
                        if (gameInventory.getButtons().keySet().contains(event.getSlot()))
                            itemBuilder = gameInventory.getButtons().get(event.getSlot());

                        if (itemBuilder == null) {
                            for (ItemBuilder items : gameInventory.getItems()) {
                                if (items.isSimilar(event.getCurrentItem())) {
                                    itemBuilder = items;
                                }
                            }
                        }
                        if (itemBuilder != null)
                            if (itemBuilder.getClickEvents() != null && !itemBuilder.getClickEvents().isEmpty())
                                for (ClickEvent clickEvent : itemBuilder.getClickEvents()) {
                                    boolean contains = false;
                                    for (ClickEvent.Type clickType : clickEvent.getClickTypes())
                                        if (event.getClick() == clickType.getClickType())
                                            contains = true;
                                    if (contains || clickEvent.getClickTypes().contains(ClickEvent.Type.ANY))
                                        clickEvent.getAction().run();
                                }
                    }
                }
            } else if (InventoryBuilder.getInventoryUsers().keySet().contains(player))
                event.setCancelled(true);
        }
    }
}