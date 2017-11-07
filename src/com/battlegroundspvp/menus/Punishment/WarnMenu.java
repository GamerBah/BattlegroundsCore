package com.battlegroundspvp.menus.Punishment;
/* Created by GamerBah on 8/25/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.comphenix.packetwrapper.WrapperPlayServerOpenSignEditor;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.entity.Player;

public class WarnMenu {
    private BattlegroundsCore plugin;

    public WarnMenu(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    private static void openSearch(Player player) {
        WrapperPlayServerOpenSignEditor openSignEditor = new WrapperPlayServerOpenSignEditor();
        openSignEditor.setLocation(new BlockPosition(0, 0, 0));
        openSignEditor.sendPacket(player);
    }

    /*public void openPlayersMenu(Player player, PunishmentMenus.SortType sortType, String searchTerm, int page) {
        GameProfile pData = plugin.getGameProfile(player.getUniqueId());
        Inventory inv = plugin.getServer().createInventory(null, 54, "Warn Menu");

        int a = 0;
        for (int i = page * 45; i < plugin.getGameProfile().size() && i >= page * 45 && i < (page + 1) * 45; i++) {
            Collections.sort(plugin.getGameProfile(), new Comparator<GameProfile>() {
                @Override
                public int compare(GameProfile p1, GameProfile p2) {
                    if (sortType.equals(PunishmentMenus.SortType.NAME_ZA)) {
                        return p2.getName().compareTo(p1.getName());
                    } else if (sortType.equals(PunishmentMenus.SortType.RANK_HIGH_LOW)) {
                        return p1.getRank().compareTo(p2.getRank());
                    } else if (sortType.equals(PunishmentMenus.SortType.RANK_LOW_HIGH)) {
                        return p2.getRank().compareTo(p1.getRank());
                    } else {
                        return p1.getName().compareTo(p2.getName());
                    }
                }
            });
            GameProfile gameProfile = plugin.getGameProfile().get(i);
            if (gameProfile.getId() != pData.getId()) {
                if (i < 45) {
                    if (sortType.equals(PunishmentMenus.SortType.ONLINE_ONLY)) {
                        if (plugin.getServer().getPlayer(gameProfile.getUuid()) != null) {
                            ItemStack head = new ItemBuilder(Material.SKULL_ITEM).durability(3).name(gameProfile.getRank().getColor().create() + gameProfile.getName())
                                    .lore(ChatColor.GRAY + "Rank: " + gameProfile.getRank().getColor().create() + (gameProfile.getRank().equals(Rank.DEFAULT) ? "" : "" + ChatColor.BOLD) + gameProfile.getRank().getName())
                                    .lore(ChatColor.RED + "Warnings: " + ChatColor.GRAY + (!WarnCommand.getWarned().containsKey(gameProfile.getUuid()) ? "0" : WarnCommand.getWarned().get(gameProfile.getUuid())));
                            SkullMeta meta = (SkullMeta) head.getItemMeta();
                            meta.setOwner(gameProfile.getName());
                            head.setItemMeta(meta);

                            inv.setItem(a++, head);
                        }
                    } else if (sortType.equals(PunishmentMenus.SortType.SEARCH) && searchTerm != null) {
                        if (StringUtils.containsIgnoreCase(gameProfile.getName(), searchTerm)) {
                            ItemStack head = new ItemBuilder(Material.SKULL_ITEM).durability(3).name(gameProfile.getRank().getColor().create() + gameProfile.getName())
                                    .lore(ChatColor.GRAY + "Rank: " + gameProfile.getRank().getColor().create() + (gameProfile.getRank().equals(Rank.DEFAULT) ? "" : "" + ChatColor.BOLD) + gameProfile.getRank().getName())
                                    .lore(ChatColor.RED + "Warnings: " + ChatColor.GRAY + (!WarnCommand.getWarned().containsKey(gameProfile.getUuid()) ? "0" : WarnCommand.getWarned().get(gameProfile.getUuid())));
                            SkullMeta meta = (SkullMeta) head.getItemMeta();
                            meta.setOwner(gameProfile.getName());
                            head.setItemMeta(meta);

                            inv.setItem(a++, head);
                        }
                    } else {
                        ItemStack head = new ItemBuilder(Material.SKULL_ITEM).durability(3).name(gameProfile.getRank().getColor().create() + gameProfile.getName())
                                .lore(ChatColor.GRAY + "Rank: " + gameProfile.getRank().getColor().create() + (gameProfile.getRank().equals(Rank.DEFAULT) ? "" : "" + ChatColor.BOLD) + gameProfile.getRank().getName())
                                .lore(ChatColor.RED + "Warnings: " + ChatColor.GRAY + (!WarnCommand.getWarned().containsKey(gameProfile.getUuid()) ? "0" : WarnCommand.getWarned().get(gameProfile.getUuid())));
                        SkullMeta meta = (SkullMeta) head.getItemMeta();
                        meta.setOwner(gameProfile.getName());
                        head.setItemMeta(meta);

                        inv.setItem(a++, head);
                    }
                }
            }
        }
        if (plugin.getGameProfile().size() > (page + 1) * 45) {
            inv.setItem(53, new ItemBuilder(Material.ARROW).name(ChatColor.GRAY + "Next \u00BB"));
        }
        if (page > 0) {
            inv.setItem(45, new ItemBuilder(Material.ARROW).name(ChatColor.GRAY + "\u00AB Previous"));
        }
        inv.setItem(47, new ItemBuilder(Material.APPLE).name(ChatColor.AQUA + "Sort by Name: " + ChatColor.GRAY + (sortType.equals(PunishmentMenus.SortType.NAME_AZ) ? "Z-A" : "A-Z")));
        inv.setItem(48, new ItemBuilder(Material.EXP_BOTTLE).name(ChatColor.AQUA + "Sort by Rank: " + ChatColor.GRAY + (sortType.equals(PunishmentMenus.SortType.RANK_HIGH_LOW) ? "Low-High" : "High-Low")));
        inv.setItem(49, new ItemBuilder(Material.GOLDEN_CARROT).name(ChatColor.AQUA + "Sort by Online Players Only"));
        inv.setItem(51, new ItemBuilder(Material.SIGN).name(ChatColor.YELLOW + "Search..."));
        player.openInventory(inv);
    }

    public void openInventory(Player player, OfflinePlayer target, Punishment.Reason reason) {
        GameProfile targetProfile = plugin.getGameProfile(target.getUniqueId());
        Inventory inv = plugin.getServer().createInventory(null, 36, "Warning: " + targetProfile.getName());

        int i = 10;
        for (Punishment.Reason reasons : Punishment.Reason.values()) {
            if (!reasons.getEffectType().equals(Punishment.EffectType.BAN) && !reasons.getEffectType().equals(Punishment.EffectType.AUTO)) {
                String[] split = reasons.getDescription().split(",");
                inv.setItem(i++, new ItemBuilder((reason != null && reason.equals(reasons) ? Material.ENCHANTED_BOOK : Material.BOOK))
                        .name(ChatColor.RED + reasons.getName() + (reason != null && reason.equals(reasons) ? new ColorBuilder(ChatColor.GREEN).bold().create() + " SELECTED" : ""))
                        .lore(ChatColor.GRAY + split[0])
                        .lore(ChatColor.GRAY + split[1]));
            }
        }

        ItemStack wool = new ItemBuilder(Material.WOOL)
                .name(new ColorBuilder(ChatColor.GREEN).bold().create() + "ACCEPT & WARN").durability(5);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Warning: " + ChatColor.YELLOW + target.getName());
        if (reason != null) {
            lore.add(ChatColor.GRAY + "For: " + ChatColor.GOLD + reason.getName());
        } else {
            lore.add(ChatColor.GRAY + "For: " + ChatColor.RED + "Nothing (Select a book)");
        }
        ItemMeta im = wool.getItemMeta();
        im.setLore(lore);
        wool.setItemMeta(im);

        inv.setItem(30, new ItemBuilder(Material.ARROW).name(ChatColor.GRAY + "\u00AB Back"));
        inv.setItem(32, wool);

        player.openInventory(inv);
    }*/
}
