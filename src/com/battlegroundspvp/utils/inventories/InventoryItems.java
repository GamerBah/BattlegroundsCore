package com.battlegroundspvp.utils.inventories;
/* Created by GamerBah on 8/2/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.utils.ColorBuilder;
import com.battlegroundspvp.utils.enums.Time;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.format.DateTimeFormatter;

public class InventoryItems {

    public static ItemBuilder nextPage = new ItemBuilder(Material.ARROW).name(ChatColor.GRAY + "Next Page");
    public static ItemBuilder previousPage = new ItemBuilder(Material.ARROW).name(ChatColor.GRAY + "Previous Page");
    public static ItemBuilder back = new ItemBuilder(Material.ARROW).name(ChatColor.GRAY + "Back");
    public static ItemBuilder nothing = new ItemBuilder(Material.STAINED_GLASS_PANE).name(ChatColor.RED + "Nothing here yet!").durability(14);
    public static ItemBuilder border = new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(15);


    public static ItemBuilder playerHead(GameProfile gameProfile, GameInventory.Type type) {
        ItemBuilder head = new ItemBuilder(Material.SKULL_ITEM)
                .name(gameProfile.getRank().getColor().create() + gameProfile.getName())
                .lore(ChatColor.GRAY + "Rank: " + gameProfile.getRank().getColor().create() + gameProfile.getRank().getName())
                .durability(3);

        if (type.equals(GameInventory.Type.PUNISH_SEARCH)) {
            head.lore("")
                    .lore(new ColorBuilder(ChatColor.YELLOW).bold().italic().create()
                            + gameProfile.getPunishmentData().getMutes().size()
                            + new ColorBuilder(ChatColor.YELLOW).italic().create() + " Mute" + (gameProfile.getPunishmentData().getMutes().size() == 1 ? " " : "s ")
                            + new ColorBuilder(ChatColor.GRAY).italic().create() + "on record")
                    .lore(new ColorBuilder(ChatColor.GOLD).bold().italic().create()
                            + gameProfile.getPunishmentData().getKicks().size()
                            + new ColorBuilder(ChatColor.GOLD).italic().create() + " Kick" + (gameProfile.getPunishmentData().getKicks().size() == 1 ? " " : "s ")
                            + new ColorBuilder(ChatColor.GRAY).italic().create() + "on record")
                    .lore(new ColorBuilder(ChatColor.RED).bold().italic().create()
                            + gameProfile.getPunishmentData().getTempBans().size()
                            + new ColorBuilder(ChatColor.RED).italic().create() + " Temp-Ban" + (gameProfile.getPunishmentData().getTempBans().size() == 1 ? " " : "s ")
                            + new ColorBuilder(ChatColor.GRAY).italic().create() + "on record")
                    .lore(new ColorBuilder(ChatColor.DARK_RED).bold().italic().create()
                            + gameProfile.getPunishmentData().getBans().size()
                            + new ColorBuilder(ChatColor.DARK_RED).italic().create() + " Ban" + (gameProfile.getPunishmentData().getBans().size() == 1 ? " " : "s ")
                            + new ColorBuilder(ChatColor.GRAY).italic().create() + "on record");
        }

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(gameProfile.getPlayer());
        head.setItemMeta(meta);
        return head;
    }

    public static ItemBuilder punishItem(Punishment punishment) {
        GameProfile gameProfile = BattlegroundsCore.getInstance().getGameProfile(punishment.getEnforcerId());
        String prefix = (punishment.getType() == Punishment.Type.BAN || punishment.getType() == Punishment.Type.TEMP_BAN ? "Banned" : punishment.getType() == Punishment.Type.KICK ? "Kicked " : "Muted");

        ItemBuilder item = new ItemBuilder(Material.MAP).name(ChatColor.AQUA + punishment.getDate().minusHours(9).format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(PST)'")))
                .lore(ChatColor.GRAY + prefix + " by: " + gameProfile.getRank().getColor().create() + gameProfile.getRank().getName().toUpperCase()
                        + ChatColor.WHITE + " " + gameProfile.getName())
                .lore(ChatColor.GRAY + "Reason: " + ChatColor.GOLD + punishment.getReason().getName());
        if (punishment.getType() != Punishment.Type.KICK)
            item = new ItemBuilder(item).lore(ChatColor.GRAY + "Duration: " + ChatColor.YELLOW + Time.toString(punishment.getDuration(), false))
                    .lore(ChatColor.GRAY + "Active: " + (!punishment.isPardoned() ? new ColorBuilder(ChatColor.GREEN).bold().create() + "Yes" : ChatColor.RED + "No"));

        item.lore("").lore(ChatColor.YELLOW + "Click for options!");

        return item;
    }
}
