package com.battlegroundspvp.runnables;
/* Created by GamerBah on 8/20/2016 */


import com.battlegroundspvp.Core;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TrailRunnable implements Runnable {

    @Getter
    private static HashSet<Player> still = new HashSet<>();
    private static ArrayList<Player> disabled = new ArrayList<>();
    private Core plugin;
    private Map<Player, Location> playerLocations = new HashMap<>();

    public TrailRunnable(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        /*for (Player player : plugin.getServer().getOnlinePlayers()) {
            GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());
            playerLocations.putIfAbsent(player, player.getLocation());

            if (player.getLocation().getX() == playerLocations.get(player).getX()
                    && player.getLocation().getY() == playerLocations.get(player).getY()
                    && player.getLocation().getZ() == playerLocations.get(player).getZ()) {
                if (!still.contains(player)) {
                    playerLocations.put(player, player.getLocation());
                    still.add(player);
                    AFKRunnable.getAfkTimer().put(player, 0);
                }
            } else {
                playerLocations.put(player, player.getLocation());
                still.remove(player);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (Core.getAfk().contains(player.getUniqueId())) {
                        if (AFKRunnable.getAfkTimer().containsKey(player)) {
                            AFKRunnable.getAfkTimer().remove(player);
                            TTA_Methods.sendTitle(player, null, 0, 0, 0, null, 0, 0, 0);
                            player.sendMessage(ChatColor.GRAY + "You are no longer AFK");
                            EventSound.playSound(player, EventSound.CLICK);
                            Core.getAfk().remove(player.getUniqueId());
                            plugin.respawn(player);
                            player.removePotionEffect(PotionEffectType.INVISIBILITY);
                        }
                    }
                }, 2L);
            }

            if (player.getGameMode() != GameMode.CREATIVE) {
                if (gameProfile.getTrail() != null) {
                    if (!disabled.contains(player)) {
                        if (still.contains(player)) {
                            if (!Core.getAfk().contains(player.getUniqueId())) {
                                // ParticleQuality quality = gameProfile.getParticleQuality();
                                // (quality.equals(ParticleQuality.LOW) ? 3 : quality.equals(ParticleQuality.MEDIUM) ? 6 : 9)
                                // Rare


                                // Epic
                                if (gameProfile.getTrail().equals(Cosmetic.Item.TRAIL_RAIN_STORM)) {
                                    ParticleEffect.CLOUD.display(0.25F, 0.1F, 0.25F, 0, 6, player.getLocation().add(0, 2.5D, 0), 25);
                                    ParticleEffect.DRIP_WATER.display(0.15F, 0F, 0.15F, 0, 2, player.getLocation().add(0, 2.5D, 0), 25);
                                }
                                if (gameProfile.getTrail().equals(Cosmetic.Item.TRAIL_LAVA_RAIN)) {
                                    ParticleEffect.SMOKE_LARGE.display(0.25F, 0.1F, 0.25F, 0, 8, player.getLocation().add(0, 2.5D, 0), 25);
                                    ParticleEffect.DRIP_LAVA.display(0.1F, 0, 0.1F, 0, 4, player.getLocation().add(0, 2.5D, 0), 25);
                                }

                                // Legendary
                                // Flame Warrior Helix is in HelixRunnable.java
                            }
                        } else {
                            if (plugin.getConfig().getBoolean("essenceActive")) {
                                ParticleEffect.REDSTONE.display(0.2F, 0.2F, 0.2F, 1, 5, player.getLocation().add(0, 1.25, 0), 25);
                            }
                            if (gameProfile.getTrail().equals(Cosmetic.Item.TRAIL_RAIN_STORM)) {
                                ParticleEffect.DRIP_WATER.display(0.2F, 0, 0.2F, 0, 3, player.getLocation().add(0, 0.1D, 0), 25);
                                ParticleEffect.WATER_SPLASH.display(0.1F, 0, 0.1F, 0, 10, player.getLocation().add(0, 0.2D, 0), 25);
                            }
                            if (gameProfile.getTrail().equals(Cosmetic.Item.TRAIL_LAVA_RAIN)) {
                                ParticleEffect.DRIP_LAVA.display(0.2F, 0, 0.2F, 0, 3, player.getLocation().add(0, 0.1D, 0), 25);
                                ParticleEffect.LAVA.display(0.1F, 0, 0.1F, 0, 1, player.getLocation().add(0, 0.1D, 0), 25);
                            }
                            if (gameProfile.getTrail().equals(Cosmetic.Item.TRAIL_FLAME_WARRIOR)) {
                                ParticleEffect.FLAME.display(0, 0, 0, 0, 1, player.getLocation().add(0, 0.1, 0), 25);
                                ParticleEffect.FLAME.display(0, 0, 0, 0, 1, player.getLocation().add(0, 0.2, 0), 25);
                                ParticleEffect.FLAME.display(0, 0, 0, 0, 1, player.getLocation().add(0, 0.3, 0), 25);
                                ParticleEffect.SMOKE_LARGE.display(0.1F, 0.5F, 0.1F, 0, 5, player.getLocation(), 25);
                            }
                        }
                    }
                }
            }
        }*/
    }

    public void toggle(Player player) {
        if (disabled.contains(player))
            disabled.remove(player);
        else disabled.add(player);
    }
}
