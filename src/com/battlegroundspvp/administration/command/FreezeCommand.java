package com.battlegroundspvp.administration.command;
/* Created by GamerBah on 8/7/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.util.enums.EventSound;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class FreezeCommand implements CommandExecutor {
    public static boolean frozen = false;
    public static boolean reloadFreeze = false;
    public static List<Player> frozenPlayers = new ArrayList<>();

    private BattlegroundsCore plugin;

    public FreezeCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (!gameProfile.hasRank(Rank.MODERATOR) && !player.isOp()) {
            plugin.sendNoPermission(player);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/freeze <all/[username]>");
            return true;
        }

        if (args[0].equalsIgnoreCase("all")) {
            if (!frozen) {
                frozen = true;
                Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "All players have been frozen by " + player.getName() + "!");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    GameProfile pData = plugin.getGameProfile(p.getUniqueId());
                    EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                    if (!pData.hasRank(Rank.MODERATOR)) {
                        p.setWalkSpeed(0F);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -50, true, false));
                        p.setFoodLevel(6);
                        p.setSaturation(0);
                    }
                }
            } else {
                frozen = false;
                Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Movement has been re-enabled!");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    GameProfile pData = plugin.getGameProfile(p.getUniqueId());
                    if (!pData.hasRank(Rank.MODERATOR)) {
                        plugin.respawn(p);
                        p.setWalkSpeed(0.2F);
                    }
                    EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                }
                return true;
            }
        } else {
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == player) {
                player.sendMessage(ChatColor.RED + "You want to freeze yourself? But... why?");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }
            if (target == null) {
                player.sendMessage(ChatColor.RED + "That player isn't online!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            if (frozenPlayers.contains(target)) {
                frozenPlayers.remove(target);
                target.setWalkSpeed(0.2F);
                target.removePotionEffect(PotionEffectType.JUMP);
                target.setFoodLevel(20);
                player.setSaturation(20);
                plugin.respawn(target, target.getWorld().getSpawnLocation());
                target.sendMessage(ChatColor.RED + "Your movement has been re-enabled!");
                EventSound.playSound(player, EventSound.ACTION_SUCCESS);

                player.sendMessage(ChatColor.GREEN + "You unfroze " + target.getName());
                EventSound.playSound(player, EventSound.ACTION_SUCCESS);
            } else {
                frozenPlayers.add(target);
                target.setWalkSpeed(0F);
                target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 6000, -10, true, false));
                target.setFoodLevel(6);
                player.setSaturation(0);
                target.sendMessage(ChatColor.RED + "You have been frozen by " + player.getName() + "!");
                target.sendMessage(ChatColor.RED + "You will automatically be unfrozen in 5 minutes.");
                EventSound.playSound(player, EventSound.ACTION_SUCCESS);

                player.sendMessage(ChatColor.GREEN + "You have successfully frozen " + target.getName() + "!");
                player.sendMessage(ChatColor.RED + "They will automatically be unfrozen in 5 minutes.");
                EventSound.playSound(player, EventSound.ACTION_SUCCESS);


                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (frozenPlayers.contains(target)) {
                        frozenPlayers.remove(target);
                        target.setWalkSpeed(0.2F);
                        target.removePotionEffect(PotionEffectType.JUMP);
                        target.setFoodLevel(20);
                        player.setSaturation(20);
                        plugin.respawn(target, target.getWorld().getSpawnLocation());
                        target.sendMessage(ChatColor.RED + "Your movement has been automatically re-enabled!");
                        EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                    }
                }, 6000L);
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntityType().equals(EntityType.PLAYER))) {
            return;
        }

        if (frozen || frozenPlayers.contains(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerHurtEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntityType().equals(EntityType.PLAYER) && event.getDamager().getType().equals(EntityType.PLAYER))) {
            return;
        }

        Player damager = (Player) event.getDamager();
        Player damaged = (Player) event.getEntity();

        if (frozen) {
            damager.sendMessage(ChatColor.RED + "All players are frozen. You are unable to attack!");
            event.setCancelled(true);
        } else if (frozenPlayers.contains(damaged)) {
            damager.sendMessage(ChatColor.RED + "That player was frozen by a Staff member. You are unable to attack them.");
            event.setCancelled(true);
        } else if (frozenPlayers.contains(damager)) {
            damager.sendMessage(ChatColor.RED + "You are unable to attack since you are frozen.");
            event.setCancelled(true);
        }
    }
}
