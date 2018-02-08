package com.battlegroundspvp.menus.crates;
/* Created by GamerBah on 10/13/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.enums.Rarity;
import com.battlegroundspvp.utils.inventories.ClickEvent;
import com.battlegroundspvp.utils.inventories.GameInventory;
import com.battlegroundspvp.utils.inventories.InventoryBuilder;
import com.battlegroundspvp.utils.inventories.InventoryItems;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ForgeMenu extends GameInventory {

    public ForgeMenu(Player player, Location location) {
        super("Crate Forge", BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId()).getCratesData().getTotal(), 36, new CrateMenu(player, location));

        addButton(10, InventoryItems.crateItem(player, Rarity.COMMON, true, null));
        addButton(12, InventoryItems.crateItem(player, Rarity.RARE, true, null));
        addButton(14, InventoryItems.crateItem(player, Rarity.EPIC, true, null));
        addButton(16, InventoryItems.crateItem(player, Rarity.LEGENDARY, true, null));

        addButton(27, InventoryItems.back.clone().clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
            new InventoryBuilder(player, new CrateMenu(player, location)).open();
            EventSound.playSound(player, EventSound.INVENTORY_GO_BACK);
        })));
    }
}
