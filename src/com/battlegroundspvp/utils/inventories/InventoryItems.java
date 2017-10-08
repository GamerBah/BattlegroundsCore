package com.battlegroundspvp.utils.inventories;
/* Created by GamerBah on 8/2/2017 */

import com.battlegroundspvp.BattlegroundsCore;
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


    public static ItemBuilder playerHead(GameProfile gameProfile, GameInventory.Type type) {
        ItemBuilder head = new ItemBuilder(Material.SKULL_ITEM)
                .name(gameProfile.getRank().getColor().create() + gameProfile.getName())
                .lore(ChatColor.GRAY + "Rank: " + gameProfile.getRank().getColor().create() + gameProfile.getRank().getName())
                .durability(3);

        if (type.equals(GameInventory.Type.PUNISH_SEARCH)) {
            head.lore("")
                    .lore(new ColorBuilder(ChatColor.YELLOW).bold().italic().create()
                            + (BattlegroundsCore.getInstance().getMutes(gameProfile) != null ? BattlegroundsCore.getInstance().getMutes(gameProfile).size() : 0)
                            + new ColorBuilder(ChatColor.YELLOW).italic().create() + " Mutes " + new ColorBuilder(ChatColor.GRAY).italic().create() + "on record")
                    .lore(new ColorBuilder(ChatColor.GOLD).bold().italic().create()
                            + (BattlegroundsCore.getInstance().getKicks(gameProfile) != null ? BattlegroundsCore.getInstance().getKicks(gameProfile).size() : 0)
                            + new ColorBuilder(ChatColor.GOLD).italic().create() + " Kicks " + new ColorBuilder(ChatColor.GRAY).italic().create() + "on record")
                    .lore(new ColorBuilder(ChatColor.RED).bold().italic().create()
                            + (BattlegroundsCore.getInstance().getTempBans(gameProfile) != null ? BattlegroundsCore.getInstance().getTempBans(gameProfile).size() : 0)
                            + new ColorBuilder(ChatColor.RED).italic().create() + " Temp-Bans " + new ColorBuilder(ChatColor.GRAY).italic().create() + "on record")
                    .lore(new ColorBuilder(ChatColor.DARK_RED).bold().italic().create()
                            + (BattlegroundsCore.getInstance().getBans(gameProfile) != null ? BattlegroundsCore.getInstance().getBans(gameProfile).size() : 0)
                            + new ColorBuilder(ChatColor.DARK_RED).italic().create() + " Bans " + new ColorBuilder(ChatColor.GRAY).italic().create() + "on record");
        }

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(gameProfile.getPlayer());
        head.setItemMeta(meta);
        return head;
    }

    public static ItemBuilder punishItem(Punishment punishment) {
        GameProfile gameProfile = BattlegroundsCore.getInstance().getGameProfile(punishment.getEnforcer());
        String prefix = (punishment.getType() == Punishment.Type.BAN || punishment.getType() == Punishment.Type.TEMP_BAN ? "Banned" : punishment.getType() == Punishment.Type.KICK ? "Kicked " : "Muted");

        ItemBuilder item = new ItemBuilder(Material.MAP).name(ChatColor.AQUA + punishment.getDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(EST)'")))
                .lore(ChatColor.GRAY + prefix + " by: " + gameProfile.getRank().getColor() + gameProfile.getRank().getName().toUpperCase()
                        + ChatColor.WHITE + " " + gameProfile.getName())
                .lore(ChatColor.GRAY + "Reason: " + ChatColor.GOLD + punishment.getReason().getName());
        if (punishment.getType() != Punishment.Type.KICK)
            item = new ItemBuilder(item).lore(ChatColor.GRAY + "Duration: " + ChatColor.YELLOW + Time.toString(punishment.getDuration() * 1000, false))
                    .lore(ChatColor.GRAY + "Active: " + (punishment.isPardoned() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));

        return item;
    }
}
