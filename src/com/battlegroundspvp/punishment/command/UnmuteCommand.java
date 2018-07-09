package com.battlegroundspvp.punishment.command;
/* Created by GamerBah on 8/10/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.manager.GameProfileManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnmuteCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public UnmuteCommand(BattlegroundsCore plugin) {
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
            if (!gameProfile.hasRank(Rank.HELPER)) {
                plugin.sendNoPermission(player);
                return true;
            }

            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "/unmute <player>");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            GameProfile targetProfile = GameProfileManager.getGameProfile(args[0]);

            if (targetProfile == null) {
                player.sendMessage(ChatColor.RED + "That player doesn't exist!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            if (targetProfile == gameProfile) {
                if (!gameProfile.hasRank(Rank.ADMIN)) {
                    player.sendMessage(ChatColor.RED + "You can't unmute yourself!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    return true;
                }
            }

            if (!targetProfile.isMuted()) {
                player.sendMessage(ChatColor.RED + "That player isn't muted!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            targetProfile.sendMessage(ChatColor.RED + " \nYou were unmuted by " + ChatColor.GOLD + player.getName());
            targetProfile.sendMessage(ChatColor.GRAY + targetProfile.getCurrentMute().getReason().getMessage() + "\n ");
            targetProfile.getCurrentMute().setPardoned(true);

            return true;
        }
        return false;
    }

}
