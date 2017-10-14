package com.battlegroundspvp.listeners;
/* Created by GamerBah on 10/12/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.commands.CrateCommand;
import com.battlegroundspvp.utils.enums.EventSound;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListeners implements Listener {

    private BattlegroundsCore plugin;

    public BlockListeners(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (CrateCommand.getRemoving().contains(player)) {
            if (block.getType().equals(Material.ENDER_CHEST)) {
                if (!plugin.getCrateLocations().contains(block.getLocation())) {
                    player.sendMessage(ChatColor.RED + "That wasn't a Crate Location!"
                            + ChatColor.GRAY + "Use" + ChatColor.RED + " /crate remove " + ChatColor.GRAY + "and try again!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    CrateCommand.getRemoving().remove(player);
                    return;
                }
                plugin.getCrateLocations().remove(block.getLocation());
                player.sendMessage(ChatColor.GREEN + "Successfully removed that Crate Location!");
                EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                CrateCommand.getRemoving().remove(player);
            } else {
                player.sendMessage(ChatColor.RED + "Crate removal cancelled");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                CrateCommand.getRemoving().remove(player);
            }
        } else {
            if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                event.setCancelled(true);
                return;
            }
            if (block.getType().equals(Material.ENDER_CHEST)) {
                if (plugin.getCrateLocations().contains(block.getLocation())) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "That Enderchest is a Battle Crate location!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (CrateCommand.getAdding().contains(player)) {
            if (block.getType().equals(Material.ENDER_CHEST)) {
                if (!block.getRelative(BlockFace.DOWN).getType().equals(Material.ENDER_PORTAL_FRAME)) {
                    player.sendMessage(ChatColor.RED + "There must be an End Portal Frame below the Enderchest!"
                            + ChatColor.GRAY + "Use" + ChatColor.RED + " /crate add " + ChatColor.GRAY + "to try again!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    CrateCommand.getAdding().remove(player);
                    return;
                }

                if (plugin.getCrateLocations().contains(block.getLocation())) {
                    player.sendMessage(ChatColor.RED + "That is already a Crate Location!"
                            + ChatColor.GRAY + "Use" + ChatColor.RED + " /crate remove " + ChatColor.GRAY + "to remove this location!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    CrateCommand.getAdding().remove(player);
                    return;
                }
                plugin.getCrateLocations().add(block.getLocation());
                player.sendMessage(ChatColor.GREEN + "Successfully added a Crate Location!");
                EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                CrateCommand.getAdding().remove(player);
            } else {
                player.sendMessage(ChatColor.RED + "Crate addition cancelled");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                CrateCommand.getAdding().remove(player);
            }
        } else {
            if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                event.setCancelled(true);
            }
        }
    }

}
