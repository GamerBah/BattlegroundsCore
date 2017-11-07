package com.battlegroundspvp.menus.Crates;
/* Created by GamerBah on 10/13/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.enums.Rarity;
import com.battlegroundspvp.utils.inventories.ClickEvent;
import com.battlegroundspvp.utils.inventories.GameInventory;
import com.battlegroundspvp.utils.inventories.InventoryBuilder;
import com.battlegroundspvp.utils.inventories.InventoryItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ForgeMenu extends GameInventory {

    public ForgeMenu(Player player, Location location) {
        super("Crate Forge", BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId()).getCratesData().getTotal(), Type.STANDARD, new CrateMenu(player, location));
        setInventory(Bukkit.getServer().createInventory(null, 36, getInventory().getName()));

        addClickableItem(10, InventoryItems.crateItem(player, Rarity.COMMON, true, null));
        addClickableItem(12, InventoryItems.crateItem(player, Rarity.RARE, true, null));
        addClickableItem(14, InventoryItems.crateItem(player, Rarity.EPIC, true, null));
        addClickableItem(16, InventoryItems.crateItem(player, Rarity.LEGENDARY, true, null));

        addClickableItem(27, InventoryItems.back.clone().clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
            new InventoryBuilder(player, new CrateMenu(player, location)).open();
            EventSound.playSound(player, EventSound.INVENTORY_GO_BACK);
        })));
    }
}
