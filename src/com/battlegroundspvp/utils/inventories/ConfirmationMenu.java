package com.battlegroundspvp.utils.inventories;
/* Created by GamerBah on 11/9/2017 */

import com.battlegroundspvp.utils.messages.ColorBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

public class ConfirmationMenu extends GameInventory {

    public ConfirmationMenu(Runnable confirmAction, Runnable cancelAction, GameInventory previousInventory) {
        super("Confirmation", 27, previousInventory);

        addButton(12, new ItemBuilder(Material.CONCRETE).durability(5)
                .name(new ColorBuilder(ChatColor.GREEN).bold().create() + "CONFIRM")
                .lore(ChatColor.GRAY + "Confirms your previous action")
                .clickEvent(new ClickEvent(ClickEvent.Type.ANY, confirmAction)));
        addButton(14, new ItemBuilder(Material.CONCRETE).durability(14)
                .name(new ColorBuilder(ChatColor.RED).bold().create() + "CANCEL")
                .lore(ChatColor.GRAY + "Cancels your previous action")
                .clickEvent(new ClickEvent(ClickEvent.Type.ANY, cancelAction)));
    }

}
