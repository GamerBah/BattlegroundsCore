package com.battlegroundspvp.runnables;
/* Created by GamerBah on 10/25/2017 */

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.donations.CrateItem;
import com.battlegroundspvp.utils.ColorBuilder;
import com.battlegroundspvp.utils.Crate;
import com.battlegroundspvp.utils.Hologram;
import com.battlegroundspvp.utils.cosmetics.Cosmetic;
import com.battlegroundspvp.utils.cosmetics.CosmeticManager;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.enums.Rarity;
import de.slikey.effectlib.util.ParticleEffect;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.util.EulerAngle;

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
    private ArmorStand itemStand;
    private ArmorStand blockStand;
    private ArmorStand toolStand;
    private Hologram rewardagram;
    private Location itemInHandLocation;
    private Location blockInHandLocation;
    private Location toolInHandLocation;

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

        BlockFace direction = ((DirectionalContainer) location.getBlock().getState().getData()).getFacing();
        float directionalYaw = 0F;
        if (direction == BlockFace.NORTH) directionalYaw = 0F;
        if (direction == BlockFace.SOUTH) directionalYaw = 180F;
        if (direction == BlockFace.WEST) directionalYaw = -90F;
        if (direction == BlockFace.EAST) directionalYaw = 90F;

        double directionalChangeX = 0;
        double directionalChangeZ = 0;
        if (direction == BlockFace.NORTH) {
            directionalChangeZ = -0.275;
            directionalChangeX = 0.2;
        }
        if (direction == BlockFace.SOUTH) {
            directionalChangeZ = 0.275;
            directionalChangeX = -0.2;
        }
        if (direction == BlockFace.WEST) {
            directionalChangeX = -0.275;
            directionalChangeZ = -0.2;
        }
        if (direction == BlockFace.EAST) {
            directionalChangeX = 0.275;
            directionalChangeZ = 0.2;
        }

        this.itemInHandLocation = location.clone().add(0.5 + directionalChangeX, -0.65, 0.5 + directionalChangeZ);
        this.itemInHandLocation.setYaw(directionalYaw);

        new CrateItem().open(location);
        this.itemStand = (ArmorStand) player.getWorld().spawnEntity(itemInHandLocation, EntityType.ARMOR_STAND);
        itemStand.setVisible(false);
        itemStand.setSmall(true);
        itemStand.setCanPickupItems(false);
        itemStand.setInvulnerable(true);
        itemStand.setCollidable(false);
        itemStand.setAI(false);
        itemStand.setGravity(false);
        itemStand.setSilent(true);
        itemStand.setArms(true);
        itemStand.setBasePlate(false);
        itemStand.setRightArmPose(new EulerAngle(Math.toRadians(270), 0, 0));
        BattlegroundsCore.getEntities().add(itemStand);

        this.blockInHandLocation = new Location(location.getWorld(),
                Math.cos(directionalYaw * -1) * 0.275 + (location.getBlockX() + 0.5), location.getY() - 0.25, Math.sin(directionalYaw * -1) * 0.275 + (location.getBlockZ() + 0.5));
        this.blockInHandLocation.setYaw(directionalYaw < 0 ? 180 : directionalYaw - 90);

        this.blockStand = (ArmorStand) player.getWorld().spawnEntity(blockInHandLocation, EntityType.ARMOR_STAND);
        blockStand.setVisible(false);
        blockStand.setSmall(true);
        blockStand.setCanPickupItems(false);
        blockStand.setInvulnerable(true);
        blockStand.setCollidable(false);
        blockStand.setAI(false);
        blockStand.setGravity(false);
        blockStand.setSilent(true);
        blockStand.setArms(true);
        blockStand.setBasePlate(false);
        blockStand.setRightArmPose(new EulerAngle(Math.toRadians(345), 0, 0));
        BattlegroundsCore.getEntities().add(blockStand);

        this.toolInHandLocation = new Location(location.getWorld(),
                Math.cos(directionalYaw * -1) * 0.275 + (location.getBlockX() + 0.5), location.getY() - 0.5, Math.sin(directionalYaw * -1) * 0.275 + (location.getBlockZ() + 0.5));
        this.toolInHandLocation.setYaw(directionalYaw < 0 ? 180 : directionalYaw - 90);

        this.toolStand = (ArmorStand) player.getWorld().spawnEntity(toolInHandLocation.add((direction == BlockFace.NORTH || direction == BlockFace.SOUTH
                ? (directionalChangeX / 2) * -1 : 0), 0, (direction == BlockFace.WEST || direction == BlockFace.EAST ? (directionalChangeZ / 2) * -1 : 0)), EntityType.ARMOR_STAND);
        toolStand.setVisible(false);
        toolStand.setSmall(true);
        toolStand.setCanPickupItems(false);
        toolStand.setInvulnerable(true);
        toolStand.setCollidable(false);
        toolStand.setAI(false);
        toolStand.setGravity(false);
        toolStand.setSilent(true);
        toolStand.setArms(true);
        toolStand.setBasePlate(false);
        toolStand.setRightArmPose(new EulerAngle(Math.toRadians(295), 0, 0));
        BattlegroundsCore.getEntities().add(toolStand);

        Crate crate = Crate.fromLocation(location);
        if (crate != null && crate.getHologram() != null)
            this.rewardagram = new Hologram(crate.getHologram().getStands().get(crate.getHologram().getStands().size() - 1)
                    .getLocation().clone().add(0, 0.75, 0), true, reward.getFullDisplayName());
    }

    public void run() {
        GameProfile gameProfile = BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId());
        gameProfile.getCratesData().removeCrate(rarity);
        for (Player players : BattlegroundsCore.getInstance().getServer().getOnlinePlayers())
            players.playSound(location, Sound.BLOCK_ENDERCHEST_OPEN, 1F, 1F);

        while (!Thread.interrupted()) {
            int random = new Random().nextInt(CosmeticManager.getRewardableCosmetics().size());

            for (Player player : Bukkit.getOnlinePlayers())
                player.playSound(location, Sound.BLOCK_NOTE_SNARE, 1F, 1.1F);
            if (CosmeticManager.getRewardableCosmetics().get(random).getItem().getType().isBlock()
                    && CosmeticManager.getRewardableCosmetics().get(random).getItem().getType() != Material.BARRIER) {
                itemStand.setItemInHand(null);
                toolStand.setItemInHand(null);
                blockStand.setItemInHand(CosmeticManager.getRewardableCosmetics().get(random).getItem());
            } else if (isTool(CosmeticManager.getRewardableCosmetics().get(random).getItem())) {
                itemStand.setItemInHand(null);
                blockStand.setItemInHand(null);
                toolStand.setItemInHand(CosmeticManager.getRewardableCosmetics().get(random).getItem());
            } else {
                blockStand.setItemInHand(null);
                toolStand.setItemInHand(null);
                itemStand.setItemInHand(CosmeticManager.getRewardableCosmetics().get(random).getItem());
            }

            if (count <= 15) {
                itemStand.teleport(itemInHandLocation.add(0, 0.05, 0));
                blockStand.teleport(blockInHandLocation.add(0, 0.05, 0));
                toolStand.teleport(toolInHandLocation.add(0, 0.05, 0));
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
                        itemStand.setItemInHand(null);
                        toolStand.setItemInHand(null);
                        blockStand.setItemInHand(reward.getItem());
                    } else if (isTool(reward.getItem())) {
                        itemStand.setItemInHand(null);
                        blockStand.setItemInHand(null);
                        toolStand.setItemInHand(reward.getItem());
                    } else {
                        blockStand.setItemInHand(null);
                        toolStand.setItemInHand(null);
                        itemStand.setItemInHand(reward.getItem());
                    }
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.rewardagram.getStands().forEach(armorStand -> armorStand.setCustomNameVisible(true));
                    int coins;
                    if (reward.getRarity() == Rarity.EPIC) {
                        location.getWorld().strikeLightningEffect(location);
                        plugin.getServer().getOnlinePlayers().forEach(p -> {
                            if (p.getLocation().getBlockY() >= 94)
                                EventSound.playSound(p, EventSound.ITEM_RECEIVE_EPIC);
                        });
                        coins = ThreadLocalRandom.current().nextInt(50, 71);
                    } else if (reward.getRarity() == Rarity.LEGENDARY) {
                        location.getWorld().strikeLightningEffect(location);
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
                        if (gameProfile.getCosmeticsData().getLobbyCosmetics().contains(reward.getId())) {
                            if (player.isOnline()) {
                                player.sendMessage(ChatColor.GRAY + "You already have " + reward.getRarity().getColor() + (reward.getRarity() == Rarity.EPIC
                                        || reward.getRarity() == Rarity.LEGENDARY ? "" + ChatColor.BOLD : "") + reward.getName()
                                        + ChatColor.GRAY + " for lobbies, so you got " + new ColorBuilder(ChatColor.LIGHT_PURPLE).bold().create() + coins + " Battle Coins"
                                        + ChatColor.GRAY + " instead");
                                for (BattleModule module : BattleModuleLoader.modules.keySet())
                                    module.updateScoreboardCoins(player, coins);
                            }
                            gameProfile.addCoins(coins);
                        } else {
                            gameProfile.getCosmeticsData().getLobbyCosmetics().add(reward.getId());
                            if (player.isOnline())
                                player.sendMessage(ChatColor.GRAY + "You received " + reward.getRarity().getColor() + (reward.getRarity() == Rarity.EPIC
                                        || reward.getRarity() == Rarity.LEGENDARY ? "" + ChatColor.BOLD : "") + reward.getName() + ChatColor.GRAY + " for use in lobbies!");
                        }
                    } else if (reward.getServerType() == Cosmetic.ServerType.KITPVP) {
                        if (gameProfile.getCosmeticsData().getKitPvpCosmetics().contains(reward.getId())) {
                            if (player.isOnline()) {
                                player.sendMessage(ChatColor.GRAY + "You already have " + reward.getRarity().getColor() + (reward.getRarity() == Rarity.EPIC
                                        || reward.getRarity() == Rarity.LEGENDARY ? "" + ChatColor.BOLD : "") + reward.getName()
                                        + ChatColor.GRAY + " for KitPvP, so you got " + new ColorBuilder(ChatColor.LIGHT_PURPLE).bold().create() + coins + " Battle Coins"
                                        + ChatColor.GRAY + " instead");
                                for (BattleModule module : BattleModuleLoader.modules.keySet())
                                    module.updateScoreboardCoins(player, coins);
                            }
                            gameProfile.addCoins(coins);
                        } else {
                            gameProfile.getCosmeticsData().getKitPvpCosmetics().add(reward.getId());
                            if (player.isOnline())
                                player.sendMessage(ChatColor.GRAY + "You received " + reward.getRarity().getColor() + (reward.getRarity() == Rarity.EPIC
                                        || reward.getRarity() == Rarity.LEGENDARY ? "" + ChatColor.BOLD : "") + reward.getName() + ChatColor.GRAY + " for use in KitPvP!");
                        }
                    }

                    if (BattlegroundsCore.getCrateOpening().keySet().contains(player))
                        BattlegroundsCore.getCrateOpening().remove(player);

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                        return;
                    }

                    BattlegroundsCore.getEntities().remove(itemStand);
                    BattlegroundsCore.getEntities().remove(blockStand);
                    BattlegroundsCore.getEntities().remove(toolStand);
                    itemStand.remove();
                    blockStand.remove();
                    toolStand.remove();
                    ParticleEffect.FIREWORKS_SPARK.display(0.1F, 0.1F, 0.1F, 0.07F, 10, location.add(0.5, 1, 0.5), 25);
                    plugin.getServer().getOnlinePlayers().forEach(p -> p.playSound(location.add(0.5, 1, 0.5), Sound.ENTITY_ITEM_PICKUP, 1F, 1.5F));
                    if (rewardagram != null)
                        rewardagram.getStands().forEach(ArmorStand::remove);

                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                        return;
                    }
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
