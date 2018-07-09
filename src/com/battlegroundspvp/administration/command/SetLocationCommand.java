package com.battlegroundspvp.administration.command;
/* Created by GamerBah on 11/12/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.manager.GameProfileManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLocationCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public SetLocationCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = GameProfileManager.getGameProfile(player.getUniqueId());

        if (gameProfile != null) {
            if (!gameProfile.hasRank(Rank.OWNER)) {
                plugin.sendNoPermission(player);
                return true;
            }

            if (args.length != 1) {
                plugin.sendIncorrectUsage(player, "/setlocation <afk|spawn>");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            Location location = new Location(player.getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY(),
                    player.getLocation().getBlockZ()).add(0.5, 0, 0.5);
            location.setYaw(player.getLocation().getYaw());
            location.setPitch(0);

            if (args[0].equalsIgnoreCase("spawn")) {
                plugin.getConfig().set("locations.spawn", location);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Spawn location set!");
                EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                return true;
            } else if (args[0].equalsIgnoreCase("afk")) {
                plugin.getConfig().set("locations.afk", location);
                plugin.saveConfig();
                player.sendMessage(ChatColor.GREEN + "AFK location set!");
                EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                return true;
            } else {
                plugin.sendIncorrectUsage(player, "/setlocation <afk|spawn>");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }
        }
        return false;
    }
}