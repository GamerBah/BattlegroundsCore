package com.battlegroundspvp.util.gui;
/* Created by GamerBah on 11/9/2017 */

import com.battlegroundspvp.util.message.MessageBuilder;
import com.gamerbah.inventorytoolkit.ClickEvent;
import com.gamerbah.inventorytoolkit.GameInventory;
import com.gamerbah.inventorytoolkit.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

public class ConfirmationMenu extends GameInventory {

    public ConfirmationMenu(Runnable confirmAction, Runnable cancelAction, GameInventory previousInventory) {
        super("Confirmation", 27, previousInventory);

        addButton(12, new ItemBuilder(Material.CONCRETE).durability(5)
                .name(new MessageBuilder(ChatColor.GREEN).bold().create() + "CONFIRM")
                .lore(ChatColor.GRAY + "Confirms your previous action")
                .onClick(new ClickEvent(confirmAction)));
        addButton(14, new ItemBuilder(Material.CONCRETE).durability(14)
                .name(new MessageBuilder(ChatColor.RED).bold().create() + "CANCEL")
                .lore(ChatColor.GRAY + "Cancels your previous action")
                .onClick(new ClickEvent(cancelAction)));
    }

}
