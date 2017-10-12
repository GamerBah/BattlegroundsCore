package com.battlegroundspvp.menus.Punishment;
/* Created by GamerBah on 8/25/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.utils.ColorBuilder;
import com.battlegroundspvp.utils.SignGUI;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.enums.Time;
import com.battlegroundspvp.utils.inventories.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PunishmentMenus {

    public class BanMenu extends GameInventory {

        public BanMenu(Player player, GameProfile targetProfile) {
            super(targetProfile.getName() + "'s Ban History", 0, Type.PUNISH_SEARCH, new SelectionMenu(player, targetProfile), 1);

            if (!targetProfile.getPunishmentData().getBans().isEmpty()) {
                for (Punishment punishment : targetProfile.getPunishmentData().getBans())
                    addSortableItem(InventoryItems.punishItem(punishment).clone()
                            .storeObject(Punishment.class, punishment)
                            .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                                new InventoryBuilder(player, new OptionsMenu(player, targetProfile, punishment)).open();
                                EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                            })));
                setItemCount(getSortables().size());
                addClickableItem(0, new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name(ChatColor.YELLOW + "Click to ban this player!")
                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile,
                                    Punishment.Type.BAN, null, 0)).sort(InventoryBuilder.SortType.NEWEST).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            } else {
                addClickableItem(22, InventoryItems.nothing.clone()
                        .lore(ChatColor.YELLOW + "Click to ban this player!")
                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile, Punishment.Type.BAN, null, 0)).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            }
        }
    }

    public class CreationMenu extends GameInventory {

        public CreationMenu(Player player, GameProfile targetProfile, Punishment.Type type, Punishment.Reason reason, int time) {
            super(type.getName() + " Creation: " + targetProfile.getName(), (type.equals(Punishment.Type.MUTE) ? new MuteMenu(player, targetProfile)
                    : type.equals(Punishment.Type.KICK) ? new KickMenu(player, targetProfile)
                    : type.equals(Punishment.Type.TEMP_BAN) ? new TempBanMenu(player, targetProfile)
                    : new BanMenu(player, targetProfile)));
            GameProfile gameProfile = BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId());
            setInventory(BattlegroundsCore.getInstance().getServer().createInventory(null, 36, getInventory().getName()));

            ItemBuilder clock = new ItemBuilder(Material.WATCH).name(ChatColor.YELLOW + "Time: " + ChatColor.GRAY + Time.toString(time * 1000, true))
                    .lore(ChatColor.GRAY + "Left-Click: " + ChatColor.RED + "-5 min " + ChatColor.DARK_RED + "" + ChatColor.ITALIC + "(Shift: -15 min)")
                    .lore(ChatColor.GRAY + "Right-Click: " + ChatColor.GREEN + "+5 min " + ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + "(Shift: +15 min)")
                    .clickEvent(new ClickEvent(ClickEvent.Type.LEFT, () -> new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reason, time - (time <= 5 * 60 ? 0 : 5 * 60))).open()))
                    .clickEvent(new ClickEvent(ClickEvent.Type.RIGHT, () -> new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reason, time + 5 * 60)).open()))
                    .clickEvent(new ClickEvent(ClickEvent.Type.SHIFT_LEFT, () -> new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reason, time - (time <= 15 * 60 ? 0 : 15 * 60))).open()))
                    .clickEvent(new ClickEvent(ClickEvent.Type.SHIFT_RIGHT, () -> new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reason, time + 15 * 60)).open()))
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
                                    new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reasons, reasons.getLength())).open();
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
                                            new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reasons, reasons.getLength())).open();
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
                                            new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reasons, reasons.getLength())).open();
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
                                    new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reasons, reasons.getLength())).open();
                                    EventSound.playSound(player, EventSound.CLICK);
                                }));
                        addClickableItem(i++, book);
                    }
                }
            }

            ItemBuilder wool = new ItemBuilder(Material.WOOL)
                    .name(new ColorBuilder(ChatColor.GREEN).bold().create() + "ACCEPT & " + type.getName().toUpperCase()).durability(5)
                    .lore(ChatColor.GRAY + "Punishing: " + ChatColor.YELLOW + targetProfile.getName())
                    .lore(ChatColor.GRAY + "For: " + (reason != null ? ChatColor.GOLD + reason.getName() : ChatColor.RED + "Nothing (Select a book)"));
            if (type.equals(Punishment.Type.MUTE) || type.equals(Punishment.Type.TEMP_BAN))
                wool.lore(ChatColor.GRAY + "Duration: " + ChatColor.AQUA + Time.toString(time * 1000, true));
            if (reason != null)
                wool.clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                    player.closeInventory();
                    if (type == Punishment.Type.MUTE)
                        targetProfile.mute(reason, time, gameProfile);
                    if (type == Punishment.Type.KICK)
                        targetProfile.kick(reason, gameProfile);
                    if (type == Punishment.Type.TEMP_BAN)
                        targetProfile.tempBan(reason, time, gameProfile);
                    if (type == Punishment.Type.BAN)
                        targetProfile.ban(reason, gameProfile);
                }));

            addClickableItem(30, InventoryItems.back.clone().clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> openPreviousInventory(player))));
            addClickableItem(32, wool);
        }
    }

    public class KickMenu extends GameInventory {

        public KickMenu(Player player, GameProfile targetProfile) {
            super(targetProfile.getName() + "'s Kick History", 0, Type.PUNISH_SEARCH, new SelectionMenu(player, targetProfile), 1);

            if (!targetProfile.getPunishmentData().getKicks().isEmpty()) {
                for (Punishment punishment : targetProfile.getPunishmentData().getKicks())
                    addSortableItem(InventoryItems.punishItem(punishment).clone()
                            .storeObject(Punishment.class, punishment)
                            .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                                new InventoryBuilder(player, new OptionsMenu(player, targetProfile, punishment)).open();
                                EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                            })));
                setItemCount(getSortables().size());
                addClickableItem(0, new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name(ChatColor.YELLOW + "Click to kick this player!")
                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile,
                                    Punishment.Type.KICK, null, 0)).sort(InventoryBuilder.SortType.NEWEST).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            } else {
                addClickableItem(22, new ItemBuilder(InventoryItems.nothing).lore("")
                        .lore(ChatColor.YELLOW + "Click to kick this player!")
                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile, Punishment.Type.KICK, null, 0)).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            }
        }
    }

    public class MuteMenu extends GameInventory {

        public MuteMenu(Player player, GameProfile targetProfile) {
            super(targetProfile.getName() + "'s Mute History", 0, Type.PUNISH_SEARCH, new SelectionMenu(player, targetProfile), 1);

            if (!targetProfile.getPunishmentData().getMutes().isEmpty()) {
                for (Punishment punishment : targetProfile.getPunishmentData().getMutes())
                    addSortableItem(InventoryItems.punishItem(punishment).clone()
                            .storeObject(Punishment.class, punishment)
                            .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                                new InventoryBuilder(player, new OptionsMenu(player, targetProfile, punishment)).open();
                                EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                            })));
                setItemCount(getSortables().size());
                addClickableItem(0, new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name(ChatColor.YELLOW + "Click to mute this player!")
                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile,
                                    Punishment.Type.MUTE, null, 0)).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            } else {
                addClickableItem(22, InventoryItems.nothing.clone()
                        .lore(ChatColor.YELLOW + "Click to mute this player!")
                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile, Punishment.Type.MUTE, null, 0)).open();
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

        public SelectionMenu(Player player, GameProfile targetProfile) {
            super("Punishing: " + targetProfile.getName(), new PunishMenu(player));
            setInventory(BattlegroundsCore.getInstance().getServer().createInventory(null, 36, getInventory().getName()));

            addClickableItem(10, new ItemBuilder(Material.BOOK).name(new ColorBuilder(ChatColor.YELLOW).bold().create() + "MUTES")
                    .lore(ChatColor.GRAY + "Past Mutes: " + ChatColor.RED + (!targetProfile.getPunishmentData().getMutes().isEmpty() ? targetProfile.getPunishmentData().getMutes().size() : 0))
                    .lore(" ").lore(ChatColor.RED + "Click to view!")
                    .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                        new InventoryBuilder(player, new MuteMenu(player, targetProfile)).sort(InventoryBuilder.SortType.NEWEST).open();
                        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                    })));
            addClickableItem(12, new ItemBuilder(Material.BOOK).name(new ColorBuilder(ChatColor.GOLD).bold().create() + "KICKS")
                    .lore(ChatColor.GRAY + "Past Kicks: " + ChatColor.RED + (!targetProfile.getPunishmentData().getKicks().isEmpty() ? targetProfile.getPunishmentData().getKicks().size() : 0))
                    .lore(" ").lore(ChatColor.RED + "Click to view!")
                    .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                        new InventoryBuilder(player, new KickMenu(player, targetProfile)).sort(InventoryBuilder.SortType.NEWEST).open();
                        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                    })));
            addClickableItem(14, new ItemBuilder(Material.SULPHUR).name(new ColorBuilder(ChatColor.RED).bold().create() + "TEMP-BANS")
                    .lore(ChatColor.GRAY + "Past Temp-Bans: " + ChatColor.RED + (!targetProfile.getPunishmentData().getTempBans().isEmpty() ? targetProfile.getPunishmentData().getTempBans().size() : 0))
                    .lore(" ").lore(ChatColor.RED + "Click to view!")
                    .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                        new InventoryBuilder(player, new TempBanMenu(player, targetProfile)).sort(InventoryBuilder.SortType.NEWEST).open();
                        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                    })));
            addClickableItem(16, new ItemBuilder(Material.BARRIER).name(new ColorBuilder(ChatColor.DARK_RED).bold().create() + "BANS")
                    .lore(ChatColor.GRAY + "Past Bans: " + ChatColor.RED + (!targetProfile.getPunishmentData().getBans().isEmpty() ? targetProfile.getPunishmentData().getBans().size() : 0))
                    .lore(" ").lore(ChatColor.RED + "Click to view!")
                    .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                        new InventoryBuilder(player, new BanMenu(player, targetProfile)).sort(InventoryBuilder.SortType.NEWEST).open();
                        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                    })));
            addClickableItem(27, InventoryItems.back.clone().clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> openPreviousInventory(player))));
        }
    }

    public class TempBanMenu extends GameInventory {

        public TempBanMenu(Player player, GameProfile targetProfile) {
            super(targetProfile.getName() + "'s Temp-Ban History", 0, Type.PUNISH_SEARCH, new SelectionMenu(player, targetProfile), 1);

            if (!targetProfile.getPunishmentData().getTempBans().isEmpty()) {
                for (Punishment punishment : targetProfile.getPunishmentData().getTempBans())
                    addSortableItem(InventoryItems.punishItem(punishment).clone()
                            .storeObject(Punishment.class, punishment)
                            .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                                new InventoryBuilder(player, new OptionsMenu(player, targetProfile, punishment)).open();
                                EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                            })));
                setItemCount(getSortables().size());
                addClickableItem(0, new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name(ChatColor.YELLOW + "Click to temp-ban this player!")
                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile,
                                    Punishment.Type.TEMP_BAN, null, 0)).sort(InventoryBuilder.SortType.NEWEST).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            } else {
                addClickableItem(22, InventoryItems.nothing.clone()
                        .lore(ChatColor.YELLOW + "Click to temp-ban this player!")
                        .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile, Punishment.Type.TEMP_BAN, null, 0)).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            }
        }
    }

    public class OptionsMenu extends GameInventory {

        public OptionsMenu(Player player, GameProfile targetProfile, Punishment punishment) {
            super("Punishment Options", 0, Type.STANDARD,
                    punishment.getType() == Punishment.Type.MUTE ? new MuteMenu(player, targetProfile) :
                            punishment.getType() == Punishment.Type.KICK ? new KickMenu(player, targetProfile) :
                                    punishment.getType() == Punishment.Type.TEMP_BAN ? new TempBanMenu(player, targetProfile) :
                                            new BanMenu(player, targetProfile));
            setInventory(BattlegroundsCore.getInstance().getServer().createInventory(null, 27, getInventory().getName()));
            addClickableItem(12, new ItemBuilder(Material.EMPTY_MAP)
                    .name(new ColorBuilder(ChatColor.AQUA).bold().create() + "Pardon")
                    .lore(ChatColor.GRAY + "Sets this punishment as pardoned")
                    .lore("")
                    .lore(ChatColor.YELLOW + "Click to pardon!")
                    .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                        if (punishment.getType() == Punishment.Type.MUTE)
                            targetProfile.unmute(BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId()), punishment);
                        if (punishment.getType() == Punishment.Type.BAN || punishment.getType() == Punishment.Type.TEMP_BAN)
                            targetProfile.unban(BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId()), punishment);
                        player.closeInventory();
                        EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                    })));
            addClickableItem(14, new ItemBuilder(Material.PAPER)
                    .name(new ColorBuilder(ChatColor.AQUA).bold().create() + "Delete")
                    .lore(ChatColor.GRAY + "Removes this punishment from")
                    .lore(ChatColor.GRAY + "the system completely")
                    .lore("")
                    .lore(new ColorBuilder(ChatColor.DARK_RED).bold().create() + "WARNING: " + new ColorBuilder(ChatColor.RED).bold().create() + "This process cannot be undone!")
                    .lore("")
                    .lore(ChatColor.YELLOW + "Click to delete!")
                    .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> {
                        targetProfile.getPunishmentData().delete(player, targetProfile, punishment);
                        player.closeInventory();
                        EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                    })));
            addClickableItem(18, InventoryItems.back.clone().clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> openPreviousInventory(player))));
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
