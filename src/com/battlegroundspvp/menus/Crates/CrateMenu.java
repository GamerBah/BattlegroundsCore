package com.battlegroundspvp.menus.Crates;
/* Created by GamerBah on 9/17/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.donations.Crate;
import com.battlegroundspvp.utils.ColorBuilder;
import com.battlegroundspvp.utils.enums.Cosmetic;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CrateMenu extends GameInventory {

    public static HashMap<Location, GameProfile> inUse = new HashMap<>();
    private BattlegroundsCore plugin = BattlegroundsCore.getInstance();
    @Getter
    private final Location location;
    @Getter
    private final GameProfile gameProfile;

    public CrateMenu(Player player, Location location) {
        super("Battle Crates", BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId()).getCratesData().getTotal(), Type.STANDARD, null);
        this.gameProfile = BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId());
        this.location = location;

        if (gameProfile.getCratesData().getTotal() > 0) {
            // TODO: Add Crates from Least to Greatest
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
                .name(new ColorBuilder(ChatColor.RED).bold().create() + "COMING SOON! " + new ColorBuilder(ChatColor.AQUA).bold().create() + "Forge")
                .lore(ChatColor.GRAY + "Use your " + ChatColor.LIGHT_PURPLE + "Battle Coins" + ChatColor.GRAY + " to forge")
                .lore(ChatColor.GRAY + "a variety of Battle Crates!")
                .lore("")
                .lore(ChatColor.GRAY + "You have " + ChatColor.LIGHT_PURPLE + gameProfile.getCoins() + " Battle Coins")
                .clickEvent(new ClickEvent(ClickEvent.Type.ANY, () -> EventSound.playSound(player, EventSound.ACTION_FAIL))));
    }


    public void open(Player player, Crate.Type type, Location location) {
        inUse.put(location, this.gameProfile);
        player.closeInventory();
        inUse.remove(location);

        List<Cosmetic.Item> cosmetics = Arrays.asList(Cosmetic.Item.values().clone());


                /*player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.5F, 1F);
                Cosmetic.Item finalCosmetic = cosmetics.get(0);
                for (Cosmetic.Item item : cosmetics) {
                    if (item.getItem().equals(inventory.getItem(13))) {
                        finalCosmetic = item;
                        if (item.getRarity().equals(Rarity.EPIC)) {
                            EventSound.playSound(player, EventSound.ITEM_RECEIVE_EPIC);
                        }
                        if (item.getRarity().equals(Rarity.LEGENDARY)) {
                            EventSound.playSound(player, EventSound.ITEM_RECEIVE_LEGENDARY);
                        }
                    }
                }
                usingCrates.remove(player);
                if (!gameProfile.getOwnedCosmetics().contains("," + finalCosmetic.getId() + ",")) {
                    gameProfile.setOwnedCosmetics(gameProfile.getOwnedCosmetics() + finalCosmetic.getId() + ",");
                    player.sendMessage(ChatColor.AQUA + "You unlocked the " + finalCosmetic.getRarity().getColor() + (finalCosmetic.getRarity() == Rarity.EPIC
                            || finalCosmetic.getRarity() == Rarity.LEGENDARY ? "" + ChatColor.BOLD : "") + finalCosmetic.getName() + ChatColor.AQUA
                            + (finalCosmetic.getGroup().equals(Cosmetic.PARTICLE_PACK) ? " Particle Pack!" : finalCosmetic.getGroup().equals(Cosmetic.KILL_EFFECT)
                            ? " Gore!" : " Warcry!"));
                    int particles = 0;
                    int warcries = 0;
                    int gores = 0;
                    for (Cosmetic.Item item : Cosmetic.Item.values()) {
                        if (item.getId() < 1000) {
                            if (gameProfile.getOwnedCosmetics().contains(item.getId() + ",")) {
                                if (item.getGroup().equals(Cosmetic.PARTICLE_PACK)) particles++;
                                if (item.getGroup().equals(Cosmetic.KILL_SOUND)) warcries++;
                                if (item.getGroup().equals(Cosmetic.KILL_EFFECT)) gores++;
                            }
                        }
                    }
                    for (Achievement.Type achievement : Achievement.Type.values()) {
                        if (achievement.getGroup().equals(Achievement.COLLECTION)) {
                            if (achievement.getName().contains("Showmanship")) {
                                if (particles >= achievement.getRequirement()) {
                                    Achievement.sendUnlockMessage(player, achievement);
                                }
                            }
                            if (achievement.getName().contains("Warcry")) {
                                if (warcries >= achievement.getRequirement()) {
                                    Achievement.sendUnlockMessage(player, achievement);
                                }
                            }
                            if (achievement.getName().contains("Savage")) {
                                if (gores >= achievement.getRequirement()) {
                                    Achievement.sendUnlockMessage(player, achievement);
                                }
                            }
                        }
                    }
                } else {
                    int souls = ThreadLocalRandom.current().nextInt(20, 35 + 1);
                    player.sendMessage(ChatColor.GRAY + "You already have the " + finalCosmetic.getRarity().getColor() + (finalCosmetic.getRarity() == Rarity.EPIC
                            || finalCosmetic.getRarity() == Rarity.LEGENDARY ? "" + ChatColor.BOLD : "") + finalCosmetic.getName()
                            + ChatColor.GRAY + (finalCosmetic.getGroup().equals(Cosmetic.PARTICLE_PACK) ? " Particle Pack" : finalCosmetic.getGroup().equals(Cosmetic.KILL_EFFECT)
                            ? " Gore" : " Warcry") + ",\n" + ChatColor.GRAY + " so you got " + new ColorBuilder(ChatColor.AQUA).bold().create() + souls + " Souls");
                    scoreboardListener.updateScoreboardSouls(player, souls);
                }
            }
        }, 30L);*/
    }

}
