package com.battlegroundspvp.punishments.commands;
/* Created by GamerBah on 8/26/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.utils.enums.EventSound;
import net.gpedro.integrations.slack.SlackMessage;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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

        @SuppressWarnings("deprecation")
        GameProfile targetData = plugin.getGameProfile(plugin.getServer().getOfflinePlayer(args[0]).getUniqueId());

        if (targetData == null) {
            player.sendMessage(ChatColor.RED + "That player is not online or doesn't exist!");
            return true;
        }

        Punishment p = null;
        if (plugin.getPlayerPunishments().containsKey(targetData.getUuid())) {
            ArrayList<Punishment> punishments = plugin.getPlayerPunishments().get(targetData.getUuid());
            if (punishments != null) {
                for (int i = 0; i < punishments.size(); i++) {
                    Punishment punishment = punishments.get(i);
                    if (punishment.getType().equals(Punishment.Type.BAN) || punishment.getType().equals(Punishment.Type.TEMP_BAN)) {
                        if (!punishment.isPardoned()) {
                            p = punishment;
                            //BattlegroundsCore.getSql().executeUpdate(Query.UPDATE_PUNISHMENT_PARDONED, true, targetData.getUuid().toString(), punishment.getType().toString(),
                                    //punishment.getDate().toString());
                            punishment.setPardoned(true);
                            plugin.slackPunishments.call(new SlackMessage(">>> _*" + player.getName() + "* unbanned *" + targetData.getName() + "*_\n*Reason for Ban:* _"
                                    + p.getReason().getName() + "_"));
                        }
                    }
                }
            }
        }

        if (p == null) {
            player.sendMessage(ChatColor.RED + "That player isn't banned!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        plugin.getServer().getOnlinePlayers().stream().filter(staff -> plugin.getGameProfile(staff.getUniqueId()).hasRank(Rank.HELPER)).forEach(staff ->
                staff.sendMessage(ChatColor.RED + player.getName() + " unbanned " + targetData.getName()));

        return true;
    }
}
