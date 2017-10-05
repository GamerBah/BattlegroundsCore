package com.battlegroundspvp.utils.inventories;
/* Created by GamerBah on 8/2/2017 */

import com.battlegroundspvp.Core;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.utils.enums.ColorBuilder;
import com.battlegroundspvp.utils.enums.Time;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.format.DateTimeFormatter;

public class InventoryItems {

    public static ItemBuilder nextPage = new ItemBuilder(Material.ARROW).name(ChatColor.GRAY + "Next Page \u00BB");
    public static ItemBuilder previousPage = new ItemBuilder(Material.ARROW).name(ChatColor.GRAY + "\u00AB Previous Page");
    public static ItemBuilder back = new ItemBuilder(Material.ARROW).name(ChatColor.GRAY + "Back");
    public static ItemBuilder nothing = new ItemBuilder(Material.STAINED_GLASS_PANE).name(ChatColor.RED + "Nothing here yet!").durability(14);
    public static ItemBuilder border = new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(15);


    public static ItemBuilder punishItem(Punishment punishment) {
        GameProfile gameProfile = Core.getInstance().getGameProfile(punishment.getEnforcer());
        String prefix = (punishment.getType() == Punishment.Type.BAN || punishment.getType() == Punishment.Type.TEMP_BAN ? "Banned" : punishment.getType() == Punishment.Type.KICK ? "Kicked " : "Muted");

        ItemBuilder item = new ItemBuilder(Material.MAP).name(ChatColor.AQUA + punishment.getDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(EST)'")))
                .lore(ChatColor.GRAY + prefix + " by: " + ColorBuilder.valueOf(gameProfile.getRank().getColor().toString()).bold() + gameProfile.getRank().getName().toUpperCase()
                        + ChatColor.WHITE + " " + gameProfile.getName())
                .lore(ChatColor.GRAY + "Reason: " + ChatColor.GOLD + punishment.getReason().getName());
        if (punishment.getType() != Punishment.Type.KICK)
            item = new ItemBuilder(item).lore(ChatColor.GRAY + "Duration: " + ChatColor.YELLOW + Time.toString(punishment.getDuration() * 1000, false))
                    .lore(ChatColor.GRAY + "Active: " + (punishment.isPardoned() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));

        return item;
    }

    public static ItemBuilder playerHead(GameProfile gameProfile, GameInventory.Type type) {
        ItemBuilder head = new ItemBuilder(Material.SKULL_ITEM)
                .name(gameProfile.getRank().getColor() + gameProfile.getName())
                .lore(ChatColor.GRAY + "Rank: " + gameProfile.getRank().getColor()
                        + (gameProfile.getRank().equals(Rank.DEFAULT) ? "" : "" + ChatColor.BOLD) + gameProfile.getRank().getName())
                .durability(3);

        if (type.equals(GameInventory.Type.PUNISH_SEARCH)) {
            head.lore("")
                    .lore(ColorBuilder.YELLOW.bold().italic().create()
                            + (Core.getInstance().getMutes(gameProfile) != null ? Core.getInstance().getMutes(gameProfile).size() : 0) + " Mutes " + ColorBuilder.GRAY.italic().create() + "on record")
                    .lore(ColorBuilder.GOLD.bold().italic().create()
                            + (Core.getInstance().getMutes(gameProfile) != null ? Core.getInstance().getMutes(gameProfile).size() : 0) + " Kicks " + ColorBuilder.GRAY.italic().create() + "on record")
                    .lore(ColorBuilder.RED.bold().italic().create()
                            + (Core.getInstance().getMutes(gameProfile) != null ? Core.getInstance().getMutes(gameProfile).size() : 0) + " Temp-Bans " + ColorBuilder.GRAY.italic().create() + "on record")
                    .lore(ColorBuilder.DARK_RED.bold().italic().create()
                            + (Core.getInstance().getMutes(gameProfile) != null ? Core.getInstance().getMutes(gameProfile).size() : 0) + " Bans " + ColorBuilder.GRAY.italic().create() + "on record");
        }

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(gameProfile.getPlayer());
        head.setItemMeta(meta);
        return head;
    }
}
