package com.battlegroundspvp.menus.bugreports;
/* Created by GamerBah on 11/12/2017 */

import com.battlegroundspvp.utils.BugReport;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.inventories.ClickEvent;
import com.battlegroundspvp.utils.inventories.GameInventory;
import com.battlegroundspvp.utils.inventories.InventoryBuilder;
import com.battlegroundspvp.utils.inventories.ItemBuilder;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BugReportCreationMenus {

    public class TypeSelectionMenu extends GameInventory {

        public TypeSelectionMenu(Player player) {
            super("Bug Report Creation", 27);

            int i = 11;
            for (BugReport.Type type : BugReport.Type.values()) {
                String[] lore = type.getDescription().split("><");
                ItemBuilder itemBuilder = new ItemBuilder(Material.CHEST).name(type.name())
                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                            new InventoryBuilder(player, new CreationMenu(player, type)).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        }));
                for (String string : lore)
                    itemBuilder.lore(ChatColor.GRAY + string);
                addButton(i += 2, itemBuilder);
            }
        }
    }

    public class CreationMenu extends GameInventory {

        @Getter
        private String bugMessage;
        @Getter
        private String recreationMessage;

        public CreationMenu(Player player, BugReport.Type type) {
            super("Bug Report Creation", 36, new TypeSelectionMenu(player));
        }
    }

}
