package com.battlegroundspvp.menus.Crates;
/* Created by GamerBah on 10/13/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.utils.ColorBuilder;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.inventories.ClickEvent;
import com.battlegroundspvp.utils.inventories.GameInventory;
import com.battlegroundspvp.utils.inventories.InventoryItems;
import com.battlegroundspvp.utils.inventories.ItemBuilder;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ForgeMenu extends GameInventory {

    @Getter
    private final GameProfile gameProfile;

    public ForgeMenu(Player player) {
        super("Battle Crates", BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId()).getCratesData().getTotal(), Type.STANDARD, null);
        this.gameProfile = BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId());

        if (gameProfile.getCratesData().getTotal() > 0) {

        } else {
            addClickableItem(22, InventoryItems.nothing.clone()
                    .lore(ChatColor.GOLD + "You don't have any Battle Crates!")
                    .lore(ChatColor.GRAY + "Purchase some from the store or")
                    .lore(ChatColor.GRAY + "forge some by clicking the anvil!")
                    .lore("")
                    .lore(ChatColor.YELLOW + "Click to get a link to the store!")
                    .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                        BaseComponent baseComponent = new TextComponent(ChatColor.YELLOW + "Click ");
                        BaseComponent button = new TextComponent(new ColorBuilder(ChatColor.RED).bold().create() + "HERE");
                        button.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "store.battlegroundspvp.com").create()));
                        baseComponent.addExtra(button);
                        baseComponent.addExtra(ChatColor.YELLOW + " to go to the store!");
                        player.spigot().sendMessage(baseComponent);
                        EventSound.playSound(player, EventSound.CLICK);
                    })));
        }
        addClickableItem(49, new ItemBuilder(Material.ANVIL)
                .name(new ColorBuilder(ChatColor.AQUA).bold().create())
                .lore(ChatColor.GRAY + "Use your Battle Coins to forge")
                .lore(ChatColor.GRAY + "a variety of Battle Crates!")
                .lore("")
                .lore(ChatColor.GRAY + "You have " + ChatColor.LIGHT_PURPLE + gameProfile.getCoins() + " Battle Coins"));
    }


}
