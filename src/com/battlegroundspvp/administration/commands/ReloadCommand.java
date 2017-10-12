package com.battlegroundspvp.administration.commands;
/* Created by GamerBah on 9/7/2016 */


import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.runnables.UpdateRunnable;
import com.battlegroundspvp.utils.ColorBuilder;
import com.battlegroundspvp.utils.enums.EventSound;
import de.Herbystar.TTA.TTA_Methods;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ReloadCommand implements CommandExecutor {
    private BattlegroundsCore plugin;

    public ReloadCommand(BattlegroundsCore plugin) {
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
        } else {
            if (args.length != 1) {
                plugin.sendIncorrectUsage(player, "/reload <server/plugin>");
                return true;
            }
            if (args[0].equalsIgnoreCase("server")) {
                plugin.getServer().broadcastMessage(new ColorBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY + "Reloading in 5 seconds. Hang in there!");
                plugin.getServer().broadcastMessage(ChatColor.GRAY + "(You'll get sent back to the spawn once the reload is complete)");
                UpdateRunnable.updating = true;
                //ScoreboardListener scoreboardListener = new ScoreboardListener(plugin);
                for (Player players : plugin.getServer().getOnlinePlayers()) {
                    players.closeInventory();
                    TTA_Methods.removeBossBar(player);
                    plugin.getServer().getScheduler().runTaskLater(plugin, player::closeInventory, 10L);
                    /*if (KSlotsMenu.usingSlots.containsKey(players)) {
                        scoreboardListener.updateScoreboardSouls(players, KSlotsMenu.usingSlots.get(player) * 400);
                        players.sendMessage(" ");
                        players.sendMessage(ChatColor.YELLOW + "To prevent data loss, your slots roll has been cancelled,");
                        players.sendMessage(ChatColor.YELLOW + "and you've been refunded " + new ColorBuilder(ChatColor.AQUA).bold().create() + KSlotsMenu.usingSlots.get(players) * 400 + " Souls");
                    }*/
                    FreezeCommand.reloadFreeze = true;
                    players.setWalkSpeed(0F);
                    players.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -50, true, false));
                    players.setFoodLevel(6);
                    players.setSaturation(0);
                    players.sendMessage(" ");
                    players.sendMessage(ChatColor.YELLOW + "To prevent data corruption, you've been frozen.");
                    players.sendMessage(ChatColor.YELLOW + "You'll be unfrozen once the reload is complete\n ");
                }
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    plugin.getServer().reload();
                    for (Player players : plugin.getServer().getOnlinePlayers()) {
                        EventSound.playSound(players, EventSound.ACTION_SUCCESS);
                        players.setWalkSpeed(0.2F);
                    }
                    plugin.getServer().broadcastMessage(new ColorBuilder(ChatColor.RED).bold().create() + "SERVER: " + ChatColor.GRAY
                            + "Server Reload was a " + new ColorBuilder(ChatColor.GREEN).bold().create() + "success" + ChatColor.GRAY + "! Now go have fun!");
                    UpdateRunnable.updating = false;
                }, 100);
            }
            return true;
        }
        return false;
    }
}
