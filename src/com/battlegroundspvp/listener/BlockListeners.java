package com.battlegroundspvp.listener;
/* Created by GamerBah on 10/12/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.command.CrateCommand;
import com.battlegroundspvp.util.BattleCrate;
import com.battlegroundspvp.util.BattleCrateManager;
import com.battlegroundspvp.util.enums.EventSound;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
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
                BattleCrate removal = null;
                for (BattleCrate battleCrate : BattleCrateManager.getCrates())
                    if (battleCrate.getLocation().hashCode() == block.getLocation().hashCode()) removal = battleCrate;
                if (removal == null) {
                    player.sendMessage(ChatColor.RED + "That wasn't a BattleCrate Location!"
                            + ChatColor.GRAY + " Use" + ChatColor.RED + " /crate remove " + ChatColor.GRAY + "and try again!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    CrateCommand.getRemoving().remove(player);
                    return;
                }
                removal.getHologram().getStands().forEach(ArmorStand::remove);
                BattleCrateManager.getCrates().remove(removal);
                player.sendMessage(ChatColor.GREEN + "Successfully removed that BattleCrate Location!");
                EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                CrateCommand.getRemoving().remove(player);

            } else {
                player.sendMessage(ChatColor.RED + "BattleCrate removal cancelled");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                CrateCommand.getRemoving().remove(player);
            }
        } else {
            if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                event.setCancelled(true);
                return;
            }
            if (block.getType().equals(Material.ENDER_CHEST)) {
                for (BattleCrate battleCrate : BattleCrateManager.getCrates())
                    if (battleCrate.getLocation().hashCode() == block.getLocation().hashCode()) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "That Enderchest is a Battle BattleCrate location!");
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
                            + ChatColor.GRAY + " Use" + ChatColor.RED + " /battleCrate add " + ChatColor.GRAY + "to try again!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    CrateCommand.getAdding().remove(player);
                    return;
                }

                for (BattleCrate battleCrate : BattleCrateManager.getCrates()) {
                    if (battleCrate.getLocation().hashCode() == block.getLocation().hashCode()) {
                        player.sendMessage(ChatColor.RED + "That's already a BattleCrate Location!"
                                + ChatColor.GRAY + " Use" + ChatColor.RED + " /battleCrate remove " + ChatColor.GRAY + "to remove this location!");
                        EventSound.playSound(player, EventSound.ACTION_FAIL);
                        CrateCommand.getAdding().remove(player);
                        return;
                    }
                }

                BattleCrate battleCrate = new BattleCrate(block.getLocation());

                BattleCrateManager.getCrates().add(battleCrate);

                player.sendMessage(ChatColor.GREEN + "Added a BattleCrate Location " + ChatColor.GRAY + "with an ID of " + ChatColor.YELLOW + battleCrate.getId());
                EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                CrateCommand.getAdding().remove(player);
            } else {
                player.sendMessage(ChatColor.RED + "BattleCrate addition cancelled");
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
