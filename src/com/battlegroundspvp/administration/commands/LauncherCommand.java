package com.battlegroundspvp.administration.commands;
/* Created by GamerBah on 5/19/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.utils.enums.ColorBuilder;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.messages.TextComponentMessages;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LauncherCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public LauncherCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (!gameProfile.hasRank(Rank.OWNER)) {
            plugin.sendNoPermission(player);
            return true;
        }

        if (args.length == 0) {
            plugin.sendIncorrectUsage(player, "/launcher <add/remove/list>");
            return true;
        }

        Block block = player.getLocation().getBlock();

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("add")) {
                plugin.sendIncorrectUsage(player, "/launcher add <-u/-f>");
                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                for (int x = 0; x < plugin.getULaunchers().size(); x++) {
                    if (block.getRelative(BlockFace.DOWN).getLocation().getBlockX() == plugin.getULaunchers().get(x).getBlockX()
                            && block.getRelative(BlockFace.DOWN).getLocation().getBlockY() == plugin.getULaunchers().get(x).getBlockY()
                            && block.getRelative(BlockFace.DOWN).getLocation().getBlockZ() == plugin.getULaunchers().get(x).getBlockZ()) {
                        player.sendMessage(ChatColor.GREEN + "Successfully removed " + ChatColor.AQUA + "upwards launcher" + ChatColor.GREEN
                                + " at " + ChatColor.RED + plugin.getULaunchers().get(x).getX() + ", " + plugin.getULaunchers().get(x).getY() + ", " + plugin.getULaunchers().get(x).getZ());
                        plugin.getULaunchers().remove(x);
                        plugin.getULaunchersParticle().remove(x);
                        return true;
                    }
                }
                for (int x = 0; x < plugin.getFLaunchers().size(); x++) {
                    if (block.getRelative(BlockFace.DOWN).getLocation().getBlockX() == plugin.getFLaunchers().get(x).getBlockX()
                            && block.getRelative(BlockFace.DOWN).getLocation().getBlockY() == plugin.getFLaunchers().get(x).getBlockY()
                            && block.getRelative(BlockFace.DOWN).getLocation().getBlockZ() == plugin.getFLaunchers().get(x).getBlockZ()) {
                        player.sendMessage(ChatColor.GREEN + "Successfully removed " + ChatColor.AQUA + "forwards launcher" + ChatColor.GREEN
                                + " at " + ChatColor.RED + plugin.getFLaunchers().get(x).getX() + ", " + plugin.getFLaunchers().get(x).getY() + ", " + plugin.getFLaunchers().get(x).getZ());
                        plugin.getFLaunchers().remove(x);
                        plugin.getFLaunchersParticle().remove(x);
                        return true;
                    }
                }
                player.sendMessage(ChatColor.RED + "No launcher was found at this location!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                player.sendMessage(new ColorBuilder(ChatColor.AQUA).bold().create() + "Upwards Launchers:");
                for (int x = 0; x < plugin.getULaunchers().size(); x++)
                    player.spigot().sendMessage(TextComponentMessages.launcherLocation(player, plugin.getULaunchers().get(x)));
                player.sendMessage(new ColorBuilder(ChatColor.AQUA).bold().create() + "Forwards Launchers:");
                for (int x = 0; x < plugin.getFLaunchers().size(); x++)
                    player.spigot().sendMessage(TextComponentMessages.launcherLocation(player, plugin.getFLaunchers().get(x)));
                return true;
            } else {
                plugin.sendIncorrectUsage(player, "/launcher <add/remove/list>");
                return true;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                if (block.getRelative(BlockFace.DOWN).getType().equals(Material.WATER)
                        || block.getRelative(BlockFace.DOWN).getType().equals(Material.STATIONARY_WATER)
                        || block.getRelative(BlockFace.DOWN).getType().equals(Material.LAVA)
                        || block.getRelative(BlockFace.DOWN).getType().equals(Material.STATIONARY_LAVA)
                        || block.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)
                        || !block.getType().equals(Material.AIR)) {
                    player.sendMessage(ChatColor.RED + "Launchers can only be placed in safe places!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    return true;
                }
                if (plugin.getConfig().getList("launchersUp").contains(block.getRelative(BlockFace.DOWN).getLocation())) {
                    player.sendMessage(ChatColor.RED + "An upwards launcher already exists here!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    return true;
                }
                if (plugin.getConfig().getList("launchersForward").contains(block.getRelative(BlockFace.DOWN).getLocation())) {
                    player.sendMessage(ChatColor.RED + "A forwards launcher already exists here!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    return true;
                }
                if (args[1].equalsIgnoreCase("-u")) {
                    plugin.getULaunchers().add(block.getRelative(BlockFace.DOWN).getLocation());
                    plugin.getULaunchersParticle().add(plugin.getULaunchers().get(plugin.getULaunchers().size() - 1).clone().add(0, 1, 0));
                    player.sendMessage(ChatColor.GREEN + "Successfully added " + ChatColor.AQUA + "upwards launcher" + ChatColor.GREEN
                            + " at " + ChatColor.RED + block.getX() + ", " + block.getY() + ", " + block.getZ());
                    return true;
                } else if (args[1].equalsIgnoreCase("-f")) {
                    plugin.getFLaunchers().add(block.getRelative(BlockFace.DOWN).getLocation());
                    plugin.getFLaunchersParticle().add(plugin.getFLaunchers().get(plugin.getFLaunchers().size() - 1).clone().add(0, 1, 0));
                    player.sendMessage(ChatColor.GREEN + "Successfully added " + ChatColor.AQUA + "forwards launcher" + ChatColor.GREEN
                            + " at " + ChatColor.RED + block.getX() + ", " + block.getY() + ", " + block.getZ());
                    return true;
                } else {
                    plugin.sendIncorrectUsage(player, "/launcher add <-u/-f>");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                plugin.sendIncorrectUsage(player, "/launcher remove");
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                plugin.sendIncorrectUsage(player, "/launcher list");
                return true;
            } else {
                plugin.sendIncorrectUsage(player, "/launcher <add/remove/list>");
                return true;
            }
        }

        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("add")) {
                plugin.sendIncorrectUsage(player, "/launcher add <-u/-f>");
            } else if (args[0].equalsIgnoreCase("remove")) {
                plugin.sendIncorrectUsage(player, "/launcher remove");
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                plugin.sendIncorrectUsage(player, "/launcher list");
                return true;
            } else {
                plugin.sendIncorrectUsage(player, "/launcher <add/remove/list>");
                return true;
            }
        }

        return false;
    }

}