package com.battlegroundspvp.gui.punishment;
/* Created by GamerBah on 8/25/2016 */

import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.punishment.Punishment;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Time;
import com.battlegroundspvp.util.gui.InventoryItems;
import com.battlegroundspvp.util.gui.sort.TimeSort;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.battlegroundspvp.util.message.MessageBuilder;
import com.comphenix.packetwrapper.WrapperPlayServerOpenSignEditor;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.gamerbah.inventorytoolkit.ClickEvent;
import com.gamerbah.inventorytoolkit.GameInventory;
import com.gamerbah.inventorytoolkit.InventoryBuilder;
import com.gamerbah.inventorytoolkit.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PunishmentMenus {

    public class BanMenu extends GameInventory {

        public BanMenu(Player player, GameProfile targetProfile) {
            super(targetProfile.getName() + "'s Ban History", new SelectionMenu(player, targetProfile));

            if (!targetProfile.getPunishmentData().getBans().isEmpty()) {
                for (Punishment punishment : targetProfile.getPunishmentData().getBans())
                    addItem(InventoryItems.punishItem(punishment).clone()
                            .storeObject(Punishment.class, punishment)
                            .onClick(new ClickEvent(() -> {
                                new InventoryBuilder(player, new OptionsMenu(player, targetProfile, punishment)).open();
                                EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                            })));
                setItemCount(getItems().size());
                addButton(0, new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name(ChatColor.YELLOW + "Click to ban this player!")
                        .onClick(new ClickEvent(() -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile,
                                    Punishment.Type.BAN, null, 0)).sortItems(new TimeSort()).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            } else {
                addButton(22, InventoryItems.nothing.clone()
                        .lore(ChatColor.YELLOW + "Click to ban this player!")
                        .onClick(new ClickEvent(() -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile, Punishment.Type.BAN, null, 0)).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            }
        }
    }

    public class CreationMenu extends GameInventory {

        public CreationMenu(Player player, GameProfile targetProfile, Punishment.Type type, Punishment.Reason reason, int time) {
            super(type.getName() + " Creation: " + targetProfile.getName(), 36, (type.equals(Punishment.Type.MUTE) ? new MuteMenu(player, targetProfile)
                    : type.equals(Punishment.Type.KICK) ? new KickMenu(player, targetProfile)
                    : type.equals(Punishment.Type.TEMP_BAN) ? new TempBanMenu(player, targetProfile)
                    : new BanMenu(player, targetProfile)));
            GameProfile gameProfile = GameProfileManager.getGameProfile(player.getUniqueId());

            ItemBuilder clock = new ItemBuilder(Material.WATCH).name(ChatColor.YELLOW + "Time: " + ChatColor.GRAY + Time.toString(time * 1000, true))
                    .lore(ChatColor.GRAY + "Left-Click: " + ChatColor.RED + "-5 min " + ChatColor.DARK_RED + "" + ChatColor.ITALIC + "(Shift: -15 min)")
                    .lore(ChatColor.GRAY + "Right-Click: " + ChatColor.GREEN + "+5 min " + ChatColor.DARK_GREEN + "" + ChatColor.ITALIC + "(Shift: +15 min)")
                    .onClick(new ClickEvent(() -> new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reason, time - (time <= 5 * 60 ? 0 : 5 * 60))).open(), ClickEvent.Type.LEFT))
                    .onClick(new ClickEvent(() -> new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reason, time + 5 * 60)).open(), ClickEvent.Type.RIGHT))
                    .onClick(new ClickEvent(() -> new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reason, time - (time <= 15 * 60 ? 0 : 15 * 60))).open(), ClickEvent.Type.SHIFT_LEFT))
                    .onClick(new ClickEvent(() -> new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reason, time + 15 * 60)).open()))
                    .onClick(new ClickEvent(() -> EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU)));

            if (reason == null) {
                getInventory().setItem(10, new ItemBuilder(Material.WATCH)
                        .name(ChatColor.YELLOW + "Time: " + (type.equals(Punishment.Type.BAN) ? new MessageBuilder(ChatColor.RED).bold().create() + "PERMANENT"
                                : type.equals(Punishment.Type.KICK) ? ChatColor.GRAY + "N/A" : ChatColor.GRAY + "(Select a reason first)")));
            } else {
                if (type != Punishment.Type.BAN && type != Punishment.Type.KICK) {
                    addButton(10, clock.onClick(new ClickEvent(() -> addButton(10, clock))));
                } else {
                    if (type == Punishment.Type.BAN) {
                        addButton(10, new ItemBuilder(Material.WATCH).name(ChatColor.YELLOW + "Time: " + new MessageBuilder(ChatColor.RED).bold().create() + "PERMANENT"));
                    } else {
                        addButton(10, new ItemBuilder(Material.WATCH).name(ChatColor.YELLOW + "Time: " + ChatColor.GRAY + "N/A"));
                    }
                }
            }
            int i = 11;
            for (Punishment.Reason reasons : Punishment.Reason.values()) {
                if (type.equals(Punishment.Type.MUTE)) {
                    if (reasons.getType().equals(Punishment.Type.MUTE) || reasons.getType().equals(Punishment.Type.ALL)) {
                        String[] split = reasons.getDescription().split(",");
                        ItemBuilder book = new ItemBuilder((reason != null && reason.equals(reasons) ? Material.ENCHANTED_BOOK : Material.BOOK))
                                .name(ChatColor.RED + reasons.getName() + (reason != null && reason.equals(reasons) ? new MessageBuilder(ChatColor.GREEN).bold().create() + " SELECTED" : ""))
                                .lore(ChatColor.GRAY + split[0])
                                .lore(ChatColor.GRAY + split[1])
                                .onClick(new ClickEvent(() -> {
                                    new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reasons, reasons.getLength())).open();
                                    EventSound.playSound(player, EventSound.CLICK);
                                }));
                        addButton(i++, book);
                    }
                }
                if (type.equals(Punishment.Type.BAN) || type.equals(Punishment.Type.TEMP_BAN)) {
                    if (reasons.getType().equals(Punishment.Type.BAN) || reasons.getType().equals(Punishment.Type.KICK_BAN) || reasons.getType().equals(Punishment.Type.ALL)) {
                        String[] split = reasons.getDescription().split(",");
                        if (type.equals(Punishment.Type.TEMP_BAN)) {
                            if (!reasons.equals(Punishment.Reason.MODDED_CLIENT)) {
                                ItemBuilder book = new ItemBuilder((reason != null && reason.equals(reasons) ? Material.ENCHANTED_BOOK : Material.BOOK))
                                        .name(ChatColor.RED + reasons.getName() + (reason != null && reason.equals(reasons) ? new MessageBuilder(ChatColor.GREEN).bold().create() + " SELECTED" : ""))
                                        .lore(ChatColor.GRAY + split[0])
                                        .lore(ChatColor.GRAY + split[1])
                                        .onClick(new ClickEvent(() -> {
                                            new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reasons, reasons.getLength())).open();
                                            EventSound.playSound(player, EventSound.CLICK);
                                        }));
                                addButton(i++, book);
                            }
                        } else {
                            if (!reasons.equals(Punishment.Reason.MODDED_CLIENT_SUSPECTED)) {
                                ItemBuilder book = new ItemBuilder((reason != null && reason.equals(reasons) ? Material.ENCHANTED_BOOK : Material.BOOK))
                                        .name(ChatColor.RED + reasons.getName() + (reason != null && reason.equals(reasons) ? new MessageBuilder(ChatColor.GREEN).bold().create() + " SELECTED" : ""))
                                        .lore(ChatColor.GRAY + split[0])
                                        .lore(ChatColor.GRAY + split[1])
                                        .onClick(new ClickEvent(() -> {
                                            new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reasons, reasons.getLength())).open();
                                            EventSound.playSound(player, EventSound.CLICK);
                                        }));
                                addButton(i++, book);
                            }
                        }
                    }
                }
                if (type.equals(Punishment.Type.KICK)) {
                    if (reasons.getType().equals(Punishment.Type.KICK) || reasons.getType().equals(Punishment.Type.KICK_BAN) || reasons.getType().equals(Punishment.Type.ALL)) {
                        String[] split = reasons.getDescription().split(",");
                        ItemBuilder book = new ItemBuilder((reason != null && reason.equals(reasons) ? Material.ENCHANTED_BOOK : Material.BOOK))
                                .name(ChatColor.RED + reasons.getName() + (reason != null && reason.equals(reasons) ? new MessageBuilder(ChatColor.GREEN).bold().create() + " SELECTED" : ""))
                                .lore(ChatColor.GRAY + split[0])
                                .lore(ChatColor.GRAY + split[1])
                                .onClick(new ClickEvent(() -> {
                                    new InventoryBuilder(player, new CreationMenu(player, targetProfile, type, reasons, reasons.getLength())).open();
                                    EventSound.playSound(player, EventSound.CLICK);
                                }));
                        addButton(i++, book);
                    }
                }
            }

            ItemBuilder wool = new ItemBuilder(Material.WOOL)
                    .name(new MessageBuilder(ChatColor.GREEN).bold().create() + "ACCEPT & " + type.getName().toUpperCase()).durability(5)
                    .lore(ChatColor.GRAY + "Punishing: " + ChatColor.YELLOW + targetProfile.getName())
                    .lore(ChatColor.GRAY + "For: " + (reason != null ? ChatColor.GOLD + reason.getName() : ChatColor.RED + "Nothing (Select a book)"));
            if (type.equals(Punishment.Type.MUTE) || type.equals(Punishment.Type.TEMP_BAN))
                wool.lore(ChatColor.GRAY + "Duration: " + ChatColor.AQUA + Time.toString(time * 1000, true));
            if (reason != null)
                wool.onClick(new ClickEvent(() -> {
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

            addButton(30, InventoryItems.back.clone().onClick(new ClickEvent(() -> openPreviousInventory(player))));
            addButton(32, wool);
        }
    }

    public class KickMenu extends GameInventory {

        public KickMenu(Player player, GameProfile targetProfile) {
            super(targetProfile.getName() + "'s Kick History", new SelectionMenu(player, targetProfile));

            if (!targetProfile.getPunishmentData().getKicks().isEmpty()) {
                for (Punishment punishment : targetProfile.getPunishmentData().getKicks())
                    addItem(InventoryItems.punishItem(punishment).clone()
                            .storeObject(Punishment.class, punishment)
                            .onClick(new ClickEvent(() -> {
                                new InventoryBuilder(player, new OptionsMenu(player, targetProfile, punishment)).open();
                                EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                            })));
                setItemCount(getItems().size());
                addButton(0, new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name(ChatColor.YELLOW + "Click to kick this player!")
                        .onClick(new ClickEvent(() -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile,
                                    Punishment.Type.KICK, null, 0)).sortItems(new TimeSort()).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            } else {
                addButton(22, new ItemBuilder(InventoryItems.nothing).lore("")
                        .lore(ChatColor.YELLOW + "Click to kick this player!")
                        .onClick(new ClickEvent(() -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile, Punishment.Type.KICK, null, 0)).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            }
        }
    }

    public class MuteMenu extends GameInventory {

        public MuteMenu(Player player, GameProfile targetProfile) {
            super(targetProfile.getName() + "'s Mute History", new SelectionMenu(player, targetProfile));

            if (!targetProfile.getPunishmentData().getMutes().isEmpty()) {
                for (Punishment punishment : targetProfile.getPunishmentData().getMutes())
                    addItem(InventoryItems.punishItem(punishment).clone()
                            .storeObject(Punishment.class, punishment)
                            .onClick(new ClickEvent(() -> {
                                new InventoryBuilder(player, new OptionsMenu(player, targetProfile, punishment)).open();
                                EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                            })));
                setItemCount(getItems().size());
                addButton(0, new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name(ChatColor.YELLOW + "Click to mute this player!")
                        .onClick(new ClickEvent(() -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile,
                                    Punishment.Type.MUTE, null, 0)).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            } else {
                addButton(22, InventoryItems.nothing.clone()
                        .lore(ChatColor.YELLOW + "Click to mute this player!")
                        .onClick(new ClickEvent(() -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile, Punishment.Type.MUTE, null, 0)).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            }
        }
    }

    public class PunishMenu extends GameInventory {

        public PunishMenu(Player player) {
            super("Punish Menu", GameProfileManager.getGameProfiles().size(), 54, null);

            for (int i = 0; i < super.getItemCount() && i < 36; i++) {
                GameProfile gameProfile = GameProfileManager.getGameProfiles().get(i);
                if (!gameProfile.getUuid().equals(player.getUniqueId()))
                    addItem(InventoryItems.playerHead(gameProfile).clone().lore(" ")
                            .lore(new MessageBuilder(ChatColor.YELLOW).bold().italic().create()
                                    + gameProfile.getPunishmentData().getMutes().size()
                                    + new MessageBuilder(ChatColor.YELLOW).italic().create() + " Mute" + (gameProfile.getPunishmentData().getMutes().size() == 1 ? " " : "s ")
                                    + new MessageBuilder(ChatColor.GRAY).italic().create() + "on record")
                            .lore(new MessageBuilder(ChatColor.GOLD).bold().italic().create()
                                    + gameProfile.getPunishmentData().getKicks().size()
                                    + new MessageBuilder(ChatColor.GOLD).italic().create() + " Kick" + (gameProfile.getPunishmentData().getKicks().size() == 1 ? " " : "s ")
                                    + new MessageBuilder(ChatColor.GRAY).italic().create() + "on record")
                            .lore(new MessageBuilder(ChatColor.RED).bold().italic().create()
                                    + gameProfile.getPunishmentData().getTempBans().size()
                                    + new MessageBuilder(ChatColor.RED).italic().create() + " Temp-Ban" + (gameProfile.getPunishmentData().getTempBans().size() == 1 ? " " : "s ")
                                    + new MessageBuilder(ChatColor.GRAY).italic().create() + "on record")
                            .lore(new MessageBuilder(ChatColor.DARK_RED).bold().italic().create()
                                    + gameProfile.getPunishmentData().getBans().size()
                                    + new MessageBuilder(ChatColor.DARK_RED).italic().create() + " Ban" + (gameProfile.getPunishmentData().getBans().size() == 1 ? " " : "s ")
                                    + new MessageBuilder(ChatColor.GRAY).italic().create() + "on record")
                            .onClick(new ClickEvent(() -> {
                                new InventoryBuilder(player, new SelectionMenu(player, gameProfile)).open();
                                EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                            })));
            }
            addButton(51, new ItemBuilder(Material.SIGN)
                    .name(ChatColor.YELLOW + "Search...")
                    .onClick(new ClickEvent(() -> {
                        openSearch(player);
                        EventSound.playSound(player, EventSound.CLICK);
                    })));
        }

    }

    public class SelectionMenu extends GameInventory {

        public SelectionMenu(Player player, GameProfile targetProfile) {
            super("Punishing: " + targetProfile.getName(), 36, new PunishMenu(player));

            addButton(10, new ItemBuilder(Material.BOOK).name(new MessageBuilder(ChatColor.YELLOW).bold().create() + "MUTES")
                    .lore(ChatColor.GRAY + "Past Mutes: " + ChatColor.RED + (!targetProfile.getPunishmentData().getMutes().isEmpty() ? targetProfile.getPunishmentData().getMutes().size() : 0))
                    .lore(" ").lore(ChatColor.RED + "Click to view!")
                    .onClick(new ClickEvent(() -> {
                        new InventoryBuilder(player, new MuteMenu(player, targetProfile)).sortItems(new TimeSort()).open();
                        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                    })));
            addButton(12, new ItemBuilder(Material.BOOK).name(new MessageBuilder(ChatColor.GOLD).bold().create() + "KICKS")
                    .lore(ChatColor.GRAY + "Past Kicks: " + ChatColor.RED + (!targetProfile.getPunishmentData().getKicks().isEmpty() ? targetProfile.getPunishmentData().getKicks().size() : 0))
                    .lore(" ").lore(ChatColor.RED + "Click to view!")
                    .onClick(new ClickEvent(() -> {
                        new InventoryBuilder(player, new KickMenu(player, targetProfile)).sortItems(new TimeSort()).open();
                        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                    })));
            addButton(14, new ItemBuilder(Material.SULPHUR).name(new MessageBuilder(ChatColor.RED).bold().create() + "TEMP-BANS")
                    .lore(ChatColor.GRAY + "Past Temp-Bans: " + ChatColor.RED + (!targetProfile.getPunishmentData().getTempBans().isEmpty() ? targetProfile.getPunishmentData().getTempBans().size() : 0))
                    .lore(" ").lore(ChatColor.RED + "Click to view!")
                    .onClick(new ClickEvent(() -> {
                        new InventoryBuilder(player, new TempBanMenu(player, targetProfile)).sortItems(new TimeSort()).open();
                        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                    })));
            addButton(16, new ItemBuilder(Material.BARRIER).name(new MessageBuilder(ChatColor.DARK_RED).bold().create() + "BANS")
                    .lore(ChatColor.GRAY + "Past Bans: " + ChatColor.RED + (!targetProfile.getPunishmentData().getBans().isEmpty() ? targetProfile.getPunishmentData().getBans().size() : 0))
                    .lore(" ").lore(ChatColor.RED + "Click to view!")
                    .onClick(new ClickEvent(() -> {
                        new InventoryBuilder(player, new BanMenu(player, targetProfile)).sortItems(new TimeSort()).open();
                        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                    })));
            addButton(27, InventoryItems.back.clone().onClick(new ClickEvent(() -> openPreviousInventory(player))));
        }
    }

    public class TempBanMenu extends GameInventory {

        public TempBanMenu(Player player, GameProfile targetProfile) {
            super(targetProfile.getName() + "'s Temp-Ban History", new SelectionMenu(player, targetProfile));

            if (!targetProfile.getPunishmentData().getTempBans().isEmpty()) {
                for (Punishment punishment : targetProfile.getPunishmentData().getTempBans())
                    addItem(InventoryItems.punishItem(punishment).clone()
                            .storeObject(Punishment.class, punishment)
                            .onClick(new ClickEvent(() -> {
                                new InventoryBuilder(player, new OptionsMenu(player, targetProfile, punishment)).open();
                                EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                            })));
                setItemCount(getItems().size());
                addButton(0, new ItemBuilder(Material.BOOK_AND_QUILL)
                        .name(ChatColor.YELLOW + "Click to temp-ban this player!")
                        .onClick(new ClickEvent(() -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile,
                                    Punishment.Type.TEMP_BAN, null, 0)).sortItems(new TimeSort()).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            } else {
                addButton(22, InventoryItems.nothing.clone()
                        .lore(ChatColor.YELLOW + "Click to temp-ban this player!")
                        .onClick(new ClickEvent(() -> {
                            new InventoryBuilder(player, new CreationMenu(player, targetProfile, Punishment.Type.TEMP_BAN, null, 0)).open();
                            EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);
                        })));
            }
        }
    }

    public class OptionsMenu extends GameInventory {

        public OptionsMenu(Player player, GameProfile targetProfile, Punishment punishment) {
            super("Punishment Options", 27, punishment.getType() == Punishment.Type.MUTE ? new MuteMenu(player, targetProfile) :
                            punishment.getType() == Punishment.Type.KICK ? new KickMenu(player, targetProfile) :
                                    punishment.getType() == Punishment.Type.TEMP_BAN ? new TempBanMenu(player, targetProfile) :
                                            new BanMenu(player, targetProfile));
            addButton(12, new ItemBuilder(Material.EMPTY_MAP)
                    .name(new MessageBuilder(ChatColor.AQUA).bold().create() + "Pardon")
                    .lore(ChatColor.GRAY + "Sets this punishment as pardoned")
                    .lore("")
                    .lore(ChatColor.YELLOW + "Click to pardon!")
                    .onClick(new ClickEvent(() -> {
                        if (punishment.getType() == Punishment.Type.MUTE)
                            targetProfile.unmute(GameProfileManager.getGameProfile(player.getUniqueId()), punishment);
                        if (punishment.getType() == Punishment.Type.BAN || punishment.getType() == Punishment.Type.TEMP_BAN)
                            targetProfile.unban(GameProfileManager.getGameProfile(player.getUniqueId()), punishment);
                        player.closeInventory();
                        EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                    })));
            addButton(14, new ItemBuilder(Material.PAPER)
                    .name(new MessageBuilder(ChatColor.AQUA).bold().create() + "Delete")
                    .lore(ChatColor.GRAY + "Removes this punishment from")
                    .lore(ChatColor.GRAY + "the system completely")
                    .lore("")
                    .lore(new MessageBuilder(ChatColor.DARK_RED).bold().create() + "WARNING: " + new MessageBuilder(ChatColor.RED).bold().create() + "This process cannot be undone!")
                    .lore("")
                    .lore(ChatColor.YELLOW + "Click to delete!")
                    .onClick(new ClickEvent(() -> {
                        targetProfile.getPunishmentData().delete(player, targetProfile, punishment);
                        player.closeInventory();
                        EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                    })));
            addButton(18, InventoryItems.back.clone().onClick(new ClickEvent(() -> openPreviousInventory(player))));
        }
    }

    private static void openSearch(Player player) {
        WrapperPlayServerOpenSignEditor openSignEditor = new WrapperPlayServerOpenSignEditor();
        openSignEditor.setLocation(new BlockPosition(0, 0, 0));
        openSignEditor.sendPacket(player);
    }

       /*inv.setItem(47, new ItemBuilder(Material.APPLE).name(ChatColor.AQUA + "Sort by Name: " + ChatColor.GRAY + (sortType.equals(SortType.NAME_AZ) ? "Z-A" : "A-Z")));
        inv.setItem(48, new ItemBuilder(Material.EXP_BOTTLE).name(ChatColor.AQUA + "Sort by Rank: " + ChatColor.GRAY + (sortType.equals(SortType.RANK_HIGH_LOW) ? "Low-High" : "High-Low")));
        inv.setItem(49, new ItemBuilder(Material.GOLDEN_CARROT).name(ChatColor.AQUA + "Sort by Online Players Only"));
        inv.setItem(51, new ItemBuilder(Material.SIGN).name(ChatColor.YELLOW + "Search..."));
        player.openInventory(inv);
    }*/
}
