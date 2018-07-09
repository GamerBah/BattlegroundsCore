package com.battlegroundspvp.gui.cosmetic;
/* Created by GamerBah on 10/13/2017 */

import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rarity;
import com.battlegroundspvp.util.gui.InventoryItems;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.gamerbah.inventorytoolkit.ClickEvent;
import com.gamerbah.inventorytoolkit.GameInventory;
import com.gamerbah.inventorytoolkit.InventoryBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ForgeMenu extends GameInventory {

    public ForgeMenu(Player player, Location location) {
        super("BattleCrate Forge", GameProfileManager.getGameProfile(player.getUniqueId()).getCratesData().getTotal(), 36, new CrateMenu(player, location));

        addButton(10, InventoryItems.crateItem(player, Rarity.COMMON, true, null));
        addButton(12, InventoryItems.crateItem(player, Rarity.RARE, true, null));
        addButton(14, InventoryItems.crateItem(player, Rarity.EPIC, true, null));
        addButton(16, InventoryItems.crateItem(player, Rarity.LEGENDARY, true, null));

        addButton(27, InventoryItems.back.clone().onClick(new ClickEvent(() -> {
            new InventoryBuilder(player, new CrateMenu(player, location)).open();
            EventSound.playSound(player, EventSound.INVENTORY_GO_BACK);
        })));
    }
}
