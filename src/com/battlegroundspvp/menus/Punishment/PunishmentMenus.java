package com.battlegroundspvp.menus.Punishment;
/* Created by GamerBah on 8/25/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.utils.SignGUI;
import com.battlegroundspvp.utils.enums.ColorBuilder;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.enums.Time;
import com.battlegroundspvp.utils.inventories.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PunishmentMenus {

    public class BanMenu extends GameInventory {

        public BanMenu(Player player, GameProfile targetData) {
            super(targetData.getName() + "'s Ban History", 0, Type.PUNISH_SEARCH, new SelectionMenu(player, targetData));

            if (BattlegroundsCore.getInstance().getBans(targetData) != null) {
                for (int i = 0; i < BattlegroundsCore.getInstance().getBans(targetData).size(); i++) {
                    Punishment punishment = BattlegroundsCore.getInstance().getBans(targetData).get(i);
                    addSortableItem(InventoryItems.punishItem(punishment));
                }
                setItemCount(getSortables().size());
            } else {
                addClickableItem(22, InventoryItems.nothing.clone().lore("")
                        .lore(ChatColor.YELLOW + "Click to ban this player!")
                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetData, Punishment.Type.BAN, null, 0)).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            }
        }
    }

    public class CreationMenu extends GameInventory {

        public CreationMenu(Player player, GameProfile targetData, Punishment.Type type, Punishment.Reason reason, int time) {
            super(type.getName() + " Creation: " + targetData.getName(), (type.equals(Punishment.Type.MUTE) ? new MuteMenu(player, targetData)
                    : type.equals(Punishment.Type.KICK) ? new KickMenu(player, targetData)
                    : type.equals(Punishment.Type.TEMP_BAN) ? new TempBanMenu(player, targetData)
                    : new BanMenu(player, targetData)));
            setInventory(BattlegroundsCore.getInstance().getServer().createInventory(null, 36, getInventory().getName()));

            ItemBuilder clock = new ItemBuilder(Material.WATCH).name(ChatColor.YELLOW + "Time: " + ChatColor.GRAY + Time.toString(time * 1000, true))
                    .lore(ChatColor.GRAY + "Left-Click: " + ChatColor.RED + "-5 min " + ChatColor.DARK_RED + "" + ChatColor.ITALIC + "(Shift: -15 min)")
                    .lore(ChatColor.GRAY + "Right-Click: " + ChatColor.GREEN + "+5 min " + ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + "(Shift: +15 min)")
                    .clickEvent(new ClickEvent(ClickEvent.Type.LEFT, () -> new InventoryBuilder(player, new CreationMenu(player, targetData, type, reason, time - (time <= 5 * 60 ? 0 : 5 * 60))).open()))
                    .clickEvent(new ClickEvent(ClickEvent.Type.RIGHT, () -> new InventoryBuilder(player, new CreationMenu(player, targetData, type, reason, time + 5 * 60)).open()))
                    .clickEvent(new ClickEvent(ClickEvent.Type.SHIFT_LEFT, () -> new InventoryBuilder(player, new CreationMenu(player, targetData, type, reason, time - (time <= 15 * 60 ? 0 : 15 * 60))).open()))
                    .clickEvent(new ClickEvent(ClickEvent.Type.SHIFT_RIGHT, () -> new InventoryBuilder(player, new CreationMenu(player, targetData, type, reason, time + 15 * 60)).open()))
                    .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU)));

            if (reason == null) {
                getInventory().setItem(10, new ItemBuilder(Material.WATCH)
                        .name(ChatColor.YELLOW + "Time: " + (type.equals(Punishment.Type.BAN) ? new ColorBuilder(ChatColor.RED).bold().create() + "PERMANENT"
                                : type.equals(Punishment.Type.KICK) ? ChatColor.GRAY + "N/A" : ChatColor.GRAY + "(Select a reason first)")));
            } else {
                if (type != Punishment.Type.BAN && type != Punishment.Type.KICK) {
                    addClickableItem(10, clock.clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> addClickableItem(10, clock))));
                } else {
                    if (type == Punishment.Type.BAN) {
                        addClickableItem(10, new ItemBuilder(Material.WATCH).name(ChatColor.YELLOW + "Time: " + new ColorBuilder(ChatColor.RED).bold().create() + "PERMANENT"));
                    } else {
                        addClickableItem(10, new ItemBuilder(Material.WATCH).name(ChatColor.YELLOW + "Time: " + ChatColor.GRAY + "N/A"));
                    }
                }
            }
            int i = 11;
            for (Punishment.Reason reasons : Punishment.Reason.values()) {
                if (type.equals(Punishment.Type.MUTE)) {
                    if (reasons.getType().equals(Punishment.Type.MUTE) || reasons.getType().equals(Punishment.Type.ALL)) {
                        String[] split = reasons.getDescription().split(",");
                        ItemBuilder book = new ItemBuilder((reason != null && reason.equals(reasons) ? Material.ENCHANTED_BOOK : Material.BOOK))
                                .name(ChatColor.RED + reasons.getName() + (reason != null && reason.equals(reasons) ? new ColorBuilder(ChatColor.GREEN).bold().create() + " SELECTED" : ""))
                                .lore(ChatColor.GRAY + split[0])
                                .lore(ChatColor.GRAY + split[1])
                                .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                                    new InventoryBuilder(player, new CreationMenu(player, targetData, type, reasons, reasons.getLength())).open();
                                    EventSound.playSound(player, EventSound.CLICK);
                                }));
                        addClickableItem(i++, book);
                    }
                }
                if (type.equals(Punishment.Type.BAN) || type.equals(Punishment.Type.TEMP_BAN)) {
                    if (reasons.getType().equals(Punishment.Type.BAN) || reasons.getType().equals(Punishment.Type.KICK_BAN) || reasons.getType().equals(Punishment.Type.ALL)) {
                        String[] split = reasons.getDescription().split(",");
                        if (type.equals(Punishment.Type.TEMP_BAN)) {
                            if (!reasons.equals(Punishment.Reason.MODDED_CLIENT)) {
                                ItemBuilder book = new ItemBuilder((reason != null && reason.equals(reasons) ? Material.ENCHANTED_BOOK : Material.BOOK))
                                        .name(ChatColor.RED + reasons.getName() + (reason != null && reason.equals(reasons) ? new ColorBuilder(ChatColor.GREEN).bold().create() + " SELECTED" : ""))
                                        .lore(ChatColor.GRAY + split[0])
                                        .lore(ChatColor.GRAY + split[1])
                                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                                            new InventoryBuilder(player, new CreationMenu(player, targetData, type, reasons, reasons.getLength())).open();
                                            EventSound.playSound(player, EventSound.CLICK);
                                        }));
                                addClickableItem(i++, book);
                            }
                        } else {
                            if (!reasons.equals(Punishment.Reason.MODDED_CLIENT_SUSPECTED)) {
                                ItemBuilder book = new ItemBuilder((reason != null && reason.equals(reasons) ? Material.ENCHANTED_BOOK : Material.BOOK))
                                        .name(ChatColor.RED + reasons.getName() + (reason != null && reason.equals(reasons) ? new ColorBuilder(ChatColor.GREEN).bold().create() + " SELECTED" : ""))
                                        .lore(ChatColor.GRAY + split[0])
                                        .lore(ChatColor.GRAY + split[1])
                                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                                            new InventoryBuilder(player, new CreationMenu(player, targetData, type, reasons, reasons.getLength())).open();
                                            EventSound.playSound(player, EventSound.CLICK);
                                        }));
                                addClickableItem(i++, book);
                            }
                        }
                    }
                }
                if (type.equals(Punishment.Type.KICK)) {
                    if (reasons.getType().equals(Punishment.Type.KICK) || reasons.getType().equals(Punishment.Type.KICK_BAN) || reasons.getType().equals(Punishment.Type.ALL)) {
                        String[] split = reasons.getDescription().split(",");
                        ItemBuilder book = new ItemBuilder((reason != null && reason.equals(reasons) ? Material.ENCHANTED_BOOK : Material.BOOK))
                                .name(ChatColor.RED + reasons.getName() + (reason != null && reason.equals(reasons) ? new ColorBuilder(ChatColor.GREEN).bold().create() + " SELECTED" : ""))
                                .lore(ChatColor.GRAY + split[0])
                                .lore(ChatColor.GRAY + split[1])
                                .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                                    new InventoryBuilder(player, new CreationMenu(player, targetData, type, reasons, reasons.getLength())).open();
                                    EventSound.playSound(player, EventSound.CLICK);
                                }));
                        addClickableItem(i++, book);
                    }
                }
            }

            ItemBuilder wool = new ItemBuilder(Material.WOOL)
                    .name(new ColorBuilder(ChatColor.GREEN).bold().create() + "ACCEPT & " + type.getName().toUpperCase()).durability(5)
                    .lore(ChatColor.GRAY + "Punishing: " + ChatColor.YELLOW + targetData.getName())
                    .lore(ChatColor.GRAY + "For: " + (reason != null ? ChatColor.GOLD + reason.getName() : ChatColor.RED + "Nothing (Select a book)"));
            if (type.equals(Punishment.Type.MUTE) || type.equals(Punishment.Type.TEMP_BAN))
                wool.lore(ChatColor.GRAY + "Duration: " + ChatColor.AQUA + Time.toString(time * 1000, true));
            if (reason != null)
                wool.clickEvent(new ClickEvent(ClickEvent.Type.ANY, player::closeInventory));
            //.clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> BattlegroundsCore.getInstance().createPunishment(targetData, type, LocalDateTime.now(), time, player.getUniqueId(), reason)));

            addClickableItem(30, InventoryItems.back.clone().clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> openPreviousInventory(player))));
            addClickableItem(32, wool);
        }
    }

    public class KickMenu extends GameInventory {

        public KickMenu(Player player, GameProfile targetData) {
            super(targetData.getName() + "'s Kick History", 0, Type.PUNISH_SEARCH, new SelectionMenu(player, targetData));

            if (BattlegroundsCore.getInstance().getKicks(targetData) != null) {
                for (int i = 0; i < BattlegroundsCore.getInstance().getKicks(targetData).size(); i++) {
                    Punishment punishment = BattlegroundsCore.getInstance().getKicks(targetData).get(i);
                    addSortableItem(InventoryItems.punishItem(punishment));
                }
                setItemCount(getSortables().size());
                /*ItemBuilder book = new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name(ChatColor.YELLOW + "Click to kick this player!")
                        .clickEvent(new ClickEvent(player, ClickEvent.Type.ANY,
                                () -> CustomCall.switchInventory(new InventoryBuilder(player, new CreationMenu(player, targetData, Punishment.Type.KICK))))));
                addImmovable(book);
                getInventory().setItem(0, book);*/
            } else {
                addClickableItem(22, new ItemBuilder(InventoryItems.nothing).lore("")
                        .lore(ChatColor.YELLOW + "Click to kick this player!")
                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetData, Punishment.Type.KICK, null, 0)).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            }
        }
    }

    public class MuteMenu extends GameInventory {

        public MuteMenu(Player player, GameProfile targetData) {
            super(targetData.getName() + "'s Mute History", 0, Type.PUNISH_SEARCH, new SelectionMenu(player, targetData));

            if (BattlegroundsCore.getInstance().getMutes(targetData) != null) {
                for (int i = 0; i < BattlegroundsCore.getInstance().getMutes(targetData).size(); i++) {
                    Punishment punishment = BattlegroundsCore.getInstance().getMutes(targetData).get(i);
                    addSortableItem(InventoryItems.punishItem(punishment));
                }
                setItemCount(getSortables().size());
                addClickableItem(0, new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name(ChatColor.YELLOW + "Click to mute this player!")
                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> new InventoryBuilder(player, new CreationMenu(player, targetData, Punishment.Type.MUTE, null, 0)))));
            } else {
                addClickableItem(22, InventoryItems.nothing.clone().lore("")
                        .lore(ChatColor.YELLOW + "Click to mute this player!")
                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetData, Punishment.Type.MUTE, null, 0)).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            }
        }
    }

    public class PunishMenu extends GameInventory {

        public PunishMenu(Player player) {
            super("Punish Menu", BattlegroundsCore.getGameProfiles().size(), Type.PLAYER_SEARCH, null);

            for (int i = 0; i < super.getItemCount() && i < 36; i++) {
                GameProfile gameProfile = BattlegroundsCore.getGameProfiles().get(i);
                if (!gameProfile.getUuid().equals(player.getUniqueId()))
                    addSortableItem(InventoryItems.playerHead(gameProfile, Type.PUNISH_SEARCH).clone()
                            .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                                new InventoryBuilder(player, new SelectionMenu(player, gameProfile)).open();
                                EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                            })));
            }
            addClickableItem(51, new ItemBuilder(Material.SIGN)
                    .name(ChatColor.YELLOW + "Search...")
                    .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                        openSearch(player);
                        EventSound.playSound(player, EventSound.CLICK);
                    })));
        }

    }

    public class SelectionMenu extends GameInventory {

        public SelectionMenu(Player player, GameProfile targetData) {
            super("Punishing: " + targetData.getName(), new PunishMenu(player));
            setInventory(BattlegroundsCore.getInstance().getServer().createInventory(null, 36, getInventory().getName()));

            addClickableItem(10, new ItemBuilder(Material.BOOK).name(new ColorBuilder(ChatColor.YELLOW).bold().create() + "MUTES")
                    .lore(ChatColor.GRAY + "Past Mutes: " + ChatColor.RED + (BattlegroundsCore.getInstance().getMutes(targetData) != null ? BattlegroundsCore.getInstance().getMutes(targetData) : 0))
                    .lore(" ").lore(ChatColor.RED + "Click to view!")
                    .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                        new InventoryBuilder(player, new MuteMenu(player, targetData)).open();
                        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                    })));
            addClickableItem(12, new ItemBuilder(Material.BOOK).name(new ColorBuilder(ChatColor.GOLD).bold().create() + "Kicks")
                    .lore(ChatColor.GRAY + "Past Kick: " + ChatColor.RED + (BattlegroundsCore.getInstance().getKicks(targetData) != null ? BattlegroundsCore.getInstance().getKicks(targetData) : 0))
                    .lore(" ").lore(ChatColor.RED + "Click to view!")
                    .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                        new InventoryBuilder(player, new KickMenu(player, targetData)).open();
                        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                    })));
            addClickableItem(14, new ItemBuilder(Material.SULPHUR).name(new ColorBuilder(ChatColor.RED).bold().create() + "TEMP-BANS")
                    .lore(ChatColor.GRAY + "Past Temp-Bans: " + ChatColor.RED + (BattlegroundsCore.getInstance().getTempBans(targetData) != null ? BattlegroundsCore.getInstance().getTempBans(targetData) : 0))
                    .lore(" ").lore(ChatColor.RED + "Click to view!")
                    .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                        new InventoryBuilder(player, new TempBanMenu(player, targetData)).open();
                        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                    })));
            addClickableItem(16, new ItemBuilder(Material.BARRIER).name(new ColorBuilder(ChatColor.DARK_RED).bold().create() + "BANS")
                    .lore(ChatColor.GRAY + "Past Bans: " + ChatColor.RED + (BattlegroundsCore.getInstance().getBans(targetData) != null ? BattlegroundsCore.getInstance().getBans(targetData) : 0))
                    .lore(" ").lore(ChatColor.RED + "Click to view!")
                    .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                        new InventoryBuilder(player, new BanMenu(player, targetData)).open();
                        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                    })));
            addClickableItem(27, InventoryItems.back.clone().clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> openPreviousInventory(player))));
        }
    }

    public class TempBanMenu extends GameInventory {

        public TempBanMenu(Player player, GameProfile targetData) {
            super(targetData.getName() + "'s Temp-Ban History", 0, Type.PUNISH_SEARCH, new SelectionMenu(player, targetData));

            if (BattlegroundsCore.getInstance().getTempBans(targetData) != null) {
                for (int i = 0; i < BattlegroundsCore.getInstance().getTempBans(targetData).size(); i++) {
                    Punishment punishment = BattlegroundsCore.getInstance().getTempBans(targetData).get(i);
                    addSortableItem(InventoryItems.punishItem(punishment));
                }
                setItemCount(getSortables().size());
            } else {
                addClickableItem(22, new ItemBuilder(InventoryItems.nothing).lore("")
                        .lore(ChatColor.YELLOW + "Click to temp-ban this player!")
                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetData, Punishment.Type.TEMP_BAN, null, 0)).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            }
        }
    }


    private void openSearch(Player player) {
        SignGUI.open(player);
    }

       /*inv.setItem(47, new ItemBuilder(Material.APPLE).name(ChatColor.AQUA + "Sort by Name: " + ChatColor.GRAY + (sortType.equals(SortType.NAME_AZ) ? "Z-A" : "A-Z")));
        inv.setItem(48, new ItemBuilder(Material.EXP_BOTTLE).name(ChatColor.AQUA + "Sort by Rank: " + ChatColor.GRAY + (sortType.equals(SortType.RANK_HIGH_LOW) ? "Low-High" : "High-Low")));
        inv.setItem(49, new ItemBuilder(Material.GOLDEN_CARROT).name(ChatColor.AQUA + "Sort by Online Players Only"));
        inv.setItem(51, new ItemBuilder(Material.SIGN).name(ChatColor.YELLOW + "Search..."));
        player.openInventory(inv);
    }*/
}
