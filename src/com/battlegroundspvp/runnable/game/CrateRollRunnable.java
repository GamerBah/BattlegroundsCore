package com.battlegroundspvp.runnable.game;
/* Created by GamerBah on 10/25/2017 */

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.donation.CrateItem;
import com.battlegroundspvp.util.BattleCrate;
import com.battlegroundspvp.util.RelativeDirection;
import com.battlegroundspvp.util.cosmetic.Cosmetic;
import com.battlegroundspvp.util.cosmetic.CosmeticManager;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rarity;
import com.battlegroundspvp.util.message.MessageBuilder;
import com.battlegroundspvp.util.nms.Hologram;
import de.slikey.effectlib.util.ParticleEffect;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CrateRollRunnable implements Runnable {

    private BattlegroundsCore plugin;

    private Thread thread;
    private final Player player;
    private final Rarity rarity;
    private final Location location;
    private final Cosmetic reward;
    private int sleep = 100;
    private int count = 0;
    private int timer = 0;
    private BattleCrate battleCrate;
    private Hologram rewardagram;

    public CrateRollRunnable(BattlegroundsCore plugin, Player player, Rarity rarity, Location location) {
        this.plugin = plugin;
        this.player = player;
        this.rarity = rarity;
        this.location = location;

        ArrayList<Cosmetic> possibleRewards = new ArrayList<>();
        double chance = Math.random();
        for (Cosmetic cosmetic : CosmeticManager.getRewardableCosmetics())
            if (chance <= cosmetic.getRarity().getBuffedChance(rarity) && chance > cosmetic.getRarity().getMinChance(rarity))
                possibleRewards.add(cosmetic);

        this.reward = possibleRewards.get(new Random().nextInt(possibleRewards.size() - 1));
        BattlegroundsCore.getUsingCrates().put(location, player);
        BattlegroundsCore.getCrateOpening().put(player, rarity);

        new CrateItem().open(location);
        this.battleCrate = BattleCrate.fromLocation(location);
        if (battleCrate != null && battleCrate.getHologram() != null) {
            this.rewardagram = new Hologram(battleCrate.getHologram().getStands().get(battleCrate.getHologram().getStands().size() - 1)
                    .getLocation().clone().add(0, 0.75, 0), true, reward.getFullDisplayName());
        }
    }

    public void run() {
        GameProfile gameProfile = BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId());
        gameProfile.getCratesData().removeCrate(rarity);
        plugin.getServer().getOnlinePlayers().forEach(players -> players.playSound(location, Sound.BLOCK_ENDERCHEST_OPEN, 1F, 1F));

        if (rarity.hasRarity(Rarity.EPIC)) {
            RelativeDirection[] rods = {
                    new RelativeDirection(location).towards(RelativeDirection.Rotation.UP, 1.5).towards(RelativeDirection.Rotation.LEFT, 2),
                    new RelativeDirection(location).towards(RelativeDirection.Rotation.UP, 1.5).towards(RelativeDirection.Rotation.BEHIND, 2),
                    new RelativeDirection(location).towards(RelativeDirection.Rotation.UP, 1.5).towards(RelativeDirection.Rotation.RIGHT, 2)
            };
            double interval = 0.1;
            for (double x = 0; x <= 2; x += interval) {
                plugin.getServer().getOnlinePlayers().forEach(players -> players.playSound(location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 2F, 2F));
                double y = ((-0.3125 * Math.pow(x, 3)) - Math.pow((0.5 * x) - 1, 2)) + (location.getBlockY() + 3);
                ParticleEffect.END_ROD.display(0.05F, 0.05F, 0.05F, 0, 5,
                        rods[0].towards(RelativeDirection.Rotation.RIGHT, interval).getLocation(), 25);
                ParticleEffect.END_ROD.display(0.05F, 0.05F, 0.05F, 0, 5,
                        rods[1].towards(RelativeDirection.Rotation.FRONT, interval).getLocation(), 25);
                ParticleEffect.END_ROD.display(0.05F, 0.05F, 0.05F, 0, 5,
                        rods[2].towards(RelativeDirection.Rotation.LEFT, interval).getLocation(), 25);
                rods[0].getLocation().setY(y);
                rods[1].getLocation().setY(y);
                rods[2].getLocation().setY(y);
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            plugin.getServer().getOnlinePlayers().forEach(players -> players.playSound(location, Sound.BLOCK_END_PORTAL_SPAWN, 1.5F, 0.8F));
            ParticleEffect.CLOUD.display(0.2F, 0.1F, 0.2F, 0, 10, location.clone().add(0.5, 1, 0.5), 25);
        }

        while (!Thread.interrupted()) {
            int random = new Random().nextInt(CosmeticManager.getRewardableCosmetics().size());

            for (Player player : Bukkit.getOnlinePlayers())
                player.playSound(location, Sound.BLOCK_NOTE_SNARE, 1F, 1.1F);
            if (CosmeticManager.getRewardableCosmetics().get(random).getItem().getType().isBlock()
                    && CosmeticManager.getRewardableCosmetics().get(random).getItem().getType() != Material.BARRIER) {
                battleCrate.getItemStand().setItemInHand(null);
                battleCrate.getToolStand().setItemInHand(null);
                battleCrate.getBlockStand().setItemInHand(CosmeticManager.getRewardableCosmetics().get(random).getItem());
            } else if (isTool(CosmeticManager.getRewardableCosmetics().get(random).getItem())) {
                battleCrate.getItemStand().setItemInHand(null);
                battleCrate.getBlockStand().setItemInHand(null);
                battleCrate.getToolStand().setItemInHand(CosmeticManager.getRewardableCosmetics().get(random).getItem());
            } else {
                battleCrate.getBlockStand().setItemInHand(null);
                battleCrate.getToolStand().setItemInHand(null);
                battleCrate.getItemStand().setItemInHand(CosmeticManager.getRewardableCosmetics().get(random).getItem());
            }

            if (count <= 15) {
                battleCrate.getItemStand().teleport(battleCrate.getItemInHandLocation().add(0, 0.05, 0));
                battleCrate.getBlockStand().teleport(battleCrate.getBlockInHandLocation().add(0, 0.05, 0));
                battleCrate.getToolStand().teleport(battleCrate.getToolInHandLocation().add(0, 0.05, 0));
            }

            try {
                Thread.sleep(sleep);
                if (sleep <= 150)
                    this.sleep += 1;
                else if (sleep <= 275)
                    this.sleep += 15;
                else if (sleep <= 350)
                    this.sleep += 35;
                this.count++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (sleep > 350)
                if (timer <= 2) {
                    timer++;
                } else {
                    for (Player player : Bukkit.getOnlinePlayers())
                        player.playSound(location, Sound.BLOCK_NOTE_SNARE, 1F, 1.1F);
                    if (reward.getItem().getType().isBlock() && reward.getItem().getType() != Material.BARRIER) {
                        battleCrate.getItemStand().setItemInHand(null);
                        battleCrate.getToolStand().setItemInHand(null);
                        battleCrate.getBlockStand().setItemInHand(reward.getItem());
                    } else if (isTool(reward.getItem())) {
                        battleCrate.getItemStand().setItemInHand(null);
                        battleCrate.getBlockStand().setItemInHand(null);
                        battleCrate.getToolStand().setItemInHand(reward.getItem());
                    } else {
                        battleCrate.getBlockStand().setItemInHand(null);
                        battleCrate.getToolStand().setItemInHand(null);
                        battleCrate.getItemStand().setItemInHand(reward.getItem());
                    }
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.rewardagram.getStands().forEach(armorStand -> armorStand.setCustomNameVisible(true));
                    int coins;
                    if (reward.getRarity() == Rarity.EPIC) {
                        location.getWorld().strikeLightningEffect(location.clone().add(0.5, 0, 0.5));
                        plugin.getServer().getOnlinePlayers().forEach(p -> {
                            if (p.getLocation().getBlockY() >= 94)
                                EventSound.playSound(p, EventSound.ITEM_RECEIVE_EPIC);
                        });
                        coins = ThreadLocalRandom.current().nextInt(50, 71);
                    } else if (reward.getRarity() == Rarity.LEGENDARY) {
                        location.getWorld().strikeLightningEffect(location.clone().add(0.5, 0, 0.5));
                        plugin.getServer().getOnlinePlayers().forEach(p -> {
                            if (p.getLocation().getBlockY() >= 94)
                                EventSound.playSound(p, EventSound.ITEM_RECEIVE_LEGENDARY);
                        });
                        coins = ThreadLocalRandom.current().nextInt(30, 51);
                    } else {
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.3F, 1F);
                        coins = ThreadLocalRandom.current().nextInt(15, 31);
                    }

                    if (reward.getServerType() == Cosmetic.ServerType.LOBBY) {
                        if (gameProfile.getCosmetics().contains(reward.getId())) {
                            if (player.isOnline()) {
                                player.sendMessage(ChatColor.GRAY + "You already have " + reward.getRarity().getColor() + (reward.getRarity() == Rarity.EPIC
                                        || reward.getRarity() == Rarity.LEGENDARY ? "" + ChatColor.BOLD : "") + reward.getName()
                                        + ChatColor.GRAY + " for lobbies, so you got " + new MessageBuilder(ChatColor.LIGHT_PURPLE).bold().create() + coins + " Battle Coins"
                                        + ChatColor.GRAY + " instead");
                                for (BattleModule module : BattleModuleLoader.modules.keySet())
                                    module.updateScoreboardCoins(player, coins);
                            }
                            gameProfile.addCoins(coins);
                        } else {
                            gameProfile.getCosmetics().add(reward.getId());
                            if (player.isOnline())
                                player.sendMessage(ChatColor.GRAY + "You received " + reward.getRarity().getColor() + (reward.getRarity() == Rarity.EPIC
                                        || reward.getRarity() == Rarity.LEGENDARY ? "" + ChatColor.BOLD : "") + reward.getName() + ChatColor.GRAY + " for use in lobbies!");
                        }
                    } else if (reward.getServerType() == Cosmetic.ServerType.KITPVP) {
                        if (gameProfile.getCosmetics().contains(reward.getId())) {
                            if (player.isOnline()) {
                                player.sendMessage(ChatColor.GRAY + "You already have " + reward.getRarity().getColor() + (reward.getRarity() == Rarity.EPIC
                                        || reward.getRarity() == Rarity.LEGENDARY ? "" + ChatColor.BOLD : "") + reward.getName()
                                        + ChatColor.GRAY + " for KitPvP, so you got " + new MessageBuilder(ChatColor.LIGHT_PURPLE).bold().create() + coins + " Battle Coins"
                                        + ChatColor.GRAY + " instead");
                                for (BattleModule module : BattleModuleLoader.modules.keySet())
                                    module.updateScoreboardCoins(player, coins);
                            }
                            gameProfile.addCoins(coins);
                        } else {
                            gameProfile.getCosmetics().add(reward.getId());
                            if (player.isOnline())
                                player.sendMessage(ChatColor.GRAY + "You received " + reward.getRarity().getColor() + (reward.getRarity() == Rarity.EPIC
                                        || reward.getRarity() == Rarity.LEGENDARY ? "" + ChatColor.BOLD : "") + reward.getName() + ChatColor.GRAY + " for use in KitPvP!");
                        }
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                        return;
                    }

                    ParticleEffect.FIREWORKS_SPARK.display(0.1F, 0.1F, 0.1F, 0.07F, 10, location.add(0.5, 1, 0.5), 25);
                    plugin.getServer().getOnlinePlayers().forEach(p -> p.playSound(location.add(0.5, 1, 0.5), Sound.ENTITY_ITEM_PICKUP, 1F, 1.5F));
                    battleCrate.getItemStand().setItemInHand(null);
                    battleCrate.getToolStand().setItemInHand(null);
                    battleCrate.getBlockStand().setItemInHand(null);
                    battleCrate.getItemStand().teleport(battleCrate.getItemInHandLocation().add(0, -0.8, 0));
                    battleCrate.getBlockStand().teleport(battleCrate.getBlockInHandLocation().add(0, -0.8, 0));
                    battleCrate.getToolStand().teleport(battleCrate.getToolInHandLocation().add(0, -0.8, 0));
                    if (rewardagram != null)
                        rewardagram.getStands().forEach(ArmorStand::remove);

                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                        return;
                    }
                    if (BattlegroundsCore.getCrateOpening().keySet().contains(player))
                        BattlegroundsCore.getCrateOpening().remove(player);
                    BattlegroundsCore.getUsingCrates().remove(location);
                    thread.interrupt();
                }
        }
    }

    private boolean isTool(ItemStack item) {
        if (item.getType().getMaxStackSize() == 1) {
            return !item.getType().toString().contains("BUCKET") && item.getType() != Material.FLINT_AND_STEEL
                    && !item.getType().toString().contains("BARDING") && item.getType() != Material.SHEARS
                    && item.getType() != Material.POTION;
        }
        return false;
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
            BattlegroundsCore.getActiveThreads().add(thread);
        }
    }
}
