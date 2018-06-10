package com.battlegroundspvp.punishment.command;
/* Created by GamerBah on 8/26/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.util.enums.EventSound;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnbanCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public UnbanCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (!gameProfile.hasRank(Rank.ADMIN)) {
            plugin.sendNoPermission(player);
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "/unban <player>");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        GameProfile targetProfile = plugin.getGameProfile(plugin.getServer().getOfflinePlayer(args[0]).getUniqueId());

        if (targetProfile == null) {
            player.sendMessage(ChatColor.RED + "That player is not online or doesn't exist!");
            return true;
        }

        if (!targetProfile.isBanned()) {
            player.sendMessage(ChatColor.RED + "That player isn't banned!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        targetProfile.getCurrentBan().setPardoned(true);

        //plugin.slackPunishments.displayNMS(new SlackMessage(">>> _*" + player.getName() + "* unbanned *" + targetProfile.getName() + "*_\n*Reason for Ban:* _"
        //        + targetProfile.getCurrentBan().getReason().getName() + "_"));

        plugin.getServer().getOnlinePlayers().stream().filter(staff -> plugin.getGameProfile(staff.getUniqueId()).hasRank(Rank.HELPER)).forEach(staff ->
                staff.sendMessage(ChatColor.RED + player.getName() + " unbanned " + targetProfile.getName()));

        return true;
    }
}
