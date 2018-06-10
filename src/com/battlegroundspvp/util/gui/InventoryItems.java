package com.battlegroundspvp.util.gui;
/* Created by GamerBah on 8/2/2017 */

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.donation.CrateItem;
import com.battlegroundspvp.punishment.Punishment;
import com.battlegroundspvp.runnable.game.CrateRollRunnable;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rarity;
import com.battlegroundspvp.util.enums.Time;
import com.battlegroundspvp.util.message.MessageBuilder;
import com.gamerbah.inventorytoolkit.ClickEvent;
import com.gamerbah.inventorytoolkit.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.time.format.DateTimeFormatter;

public class InventoryItems {

    public static ItemBuilder nextPage = new ItemBuilder(Material.ARROW).name(ChatColor.GRAY + "Next Page");
    public static ItemBuilder previousPage = new ItemBuilder(Material.ARROW).name(ChatColor.GRAY + "Previous Page");
    public static ItemBuilder back = new ItemBuilder(Material.ARROW).name(ChatColor.GRAY + "Back");
    public static ItemBuilder nothing = new ItemBuilder(Material.STAINED_GLASS_PANE).name(ChatColor.RED + "Nothing here yet!").durability(14);

    public static ItemBuilder border(DyeColor color) {
        return new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(Byte.toUnsignedInt(color.getWoolData()));
    }


    public static ItemBuilder playerHead(GameProfile gameProfile) {
        ItemBuilder head = new ItemBuilder(Material.SKULL_ITEM)
                .name(gameProfile.getRank().getColor().create() + gameProfile.getName())
                .lore(ChatColor.GRAY + "Rank: " + gameProfile.getRank().getColor().create() + gameProfile.getRank().getName())
                .durability(3);

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
                    .lore(ChatColor.GRAY + "Active: " + (!punishment.isPardoned() ? new MessageBuilder(ChatColor.GREEN).bold().create() + "Yes" : ChatColor.RED + "No"));

        item.lore("").lore(ChatColor.YELLOW + "Click for options!");

        return item;
    }

    public static ItemBuilder crateItem(Player player, Rarity rarity, boolean buying, @Nullable Location location) {
        GameProfile gameProfile = BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId());
        ItemBuilder crate = new ItemBuilder(Material.ENDER_CHEST).name(rarity.getCrateName()).lore(" ");
        if (rarity == Rarity.COMMON)
            crate.lore(ChatColor.GRAY + "Has a small chance of rewarding")
                    .lore(Rarity.EPIC.getColor() + "EPIC" + ChatColor.GRAY + " or " + Rarity.LEGENDARY.getColor() + "LEGENDARY" + ChatColor.GRAY + " cosmetics");
        if (rarity == Rarity.RARE)
            crate.lore(ChatColor.GRAY + "Has an improved chance of rewarding")
                    .lore(Rarity.EPIC.getColor() + "EPIC" + ChatColor.GRAY + " or " + Rarity.LEGENDARY.getColor() + "LEGENDARY" + ChatColor.GRAY + " cosmetics");
        if (rarity == Rarity.EPIC)
            crate.lore(ChatColor.GRAY + "Has a very good chance of rewarding")
                    .lore(Rarity.EPIC.getColor() + "EPIC" + ChatColor.GRAY + " or " + Rarity.LEGENDARY.getColor() + "LEGENDARY" + ChatColor.GRAY + " cosmetics");
        if (rarity == Rarity.LEGENDARY || rarity == Rarity.GIFT)
            crate.lore(ChatColor.GRAY + "Has an insane chance of rewarding")
                    .lore(Rarity.EPIC.getColor() + "EPIC" + ChatColor.GRAY + " or " + Rarity.LEGENDARY.getColor() + "LEGENDARY" + ChatColor.GRAY + " cosmetics");
        if (rarity == Rarity.SEASONAL)
            crate.lore(ChatColor.GRAY + "Rewards " + Rarity.SEASONAL.getColor() + "SEASONAL" + ChatColor.GRAY + " cosmetics only!");
        if (buying) {
            crate.lore(" ").lore(ChatColor.GRAY + "Cost: " + (gameProfile.getCoins() >= rarity.getCrateCost() ? ChatColor.LIGHT_PURPLE + "" + rarity.getCrateCost() + " Battle Coins"
                    : ChatColor.RED + "" + rarity.getCrateCost() + " Battle Coin" + (rarity.getCrateCost() != 1 ? "s" : ""))).lore(" ");
            if (gameProfile.getCoins() >= rarity.getCrateCost()) {
                crate.lore(ChatColor.GRAY + "You'll have " + ChatColor.LIGHT_PURPLE + (gameProfile.getCoins() - rarity.getCrateCost())
                        + " Battle Coin" + (gameProfile.getCoins() - rarity.getCrateCost() != 1 ? "s" : "") + ChatColor.GRAY + " remaining")
                        .lore(" ").lore(new MessageBuilder(ChatColor.YELLOW).bold().create() + "CLICK TO BUY!")
                        .onClick(new ClickEvent(() -> {
                            EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                            player.closeInventory();
                            player.sendMessage(ChatColor.GRAY + "You bought a " + rarity.getCrateName() + ChatColor.GRAY + "!");
                            gameProfile.getCratesData().addCrate(rarity, 1);
                            for (BattleModule module : BattleModuleLoader.modules.keySet())
                                module.updateScoreboardCoins(player, -rarity.getCrateCost());
                            gameProfile.addCoins(-rarity.getCrateCost());
                        }));
            } else {
                crate.lore(ChatColor.RED + "You need " + ChatColor.LIGHT_PURPLE + (rarity.getCrateCost() - gameProfile.getCoins()) + " Battle Coins" + ChatColor.RED + " to buy this!")
                        .onClick(new ClickEvent(() -> EventSound.playSound(player, EventSound.ACTION_FAIL)));
            }
        } else {
            crate.lore(" ").lore(new MessageBuilder(ChatColor.YELLOW).bold().create() + "CLICK TO OPEN!")
                    .onClick(new ClickEvent(() -> {
                        player.closeInventory();
                        if (CrateItem.isInUse(location)) {
                            EventSound.playSound(player, EventSound.ACTION_FAIL);
                            player.sendMessage(new MessageBuilder(ChatColor.RED).bold().create() + "Sorry! "
                                    + ChatColor.GRAY + "Someone is already opening a Battle BattleCrate!");
                            return;
                        }
                        EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                        BattlegroundsCore.getInstance().getServer().getScheduler().runTaskLater(BattlegroundsCore.getInstance(),
                                () -> new CrateRollRunnable(BattlegroundsCore.getInstance(), player, rarity, location).start(), 20L);
                    }));
        }
        return crate;
    }
}
