package com.battlegroundspvp.punishments.commands;
/* Created by GamerBah on 8/10/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.utils.enums.EventSound;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (!gameProfile.hasRank(Rank.HELPER)) {
            plugin.sendNoPermission(player);
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "/unmute <player>");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        @SuppressWarnings("deprecation")
        GameProfile targetData = plugin.getGameProfile(plugin.getServer().getOfflinePlayer(args[0]).getUniqueId());

        if (targetData == null) {
            player.sendMessage(ChatColor.RED + "That player is not online or doesn't exist!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (targetData == gameProfile) {
            if (!gameProfile.hasRank(Rank.OWNER)) {
                player.sendMessage(ChatColor.RED + "You can't unmute yourself!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }
        }

        Punishment p = null;
        if (plugin.getPlayerPunishments().containsKey(targetData.getUuid())) {
            ArrayList<Punishment> punishments = plugin.getPlayerPunishments().get(targetData.getUuid());
            if (punishments != null) {
                for (int i = 0; i < punishments.size(); i++) {
                    Punishment punishment = punishments.get(i);
                    if (punishment.getType().equals(Punishment.Type.MUTE)) {
                        if (!punishment.isPardoned()) {
                            p = punishment;
                            //BattlegroundsCore.getSql().executeUpdate(Query.UPDATE_PUNISHMENT_PARDONED, true, targetData.getUuid().toString(), punishment.getType().toString(), punishment.getDate().toString());
                            punishment.setPardoned(true);
                        }
                    }
                }
            }
        }

        if (p == null) {
            player.sendMessage(ChatColor.RED + "That player isn't muted!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        plugin.getServer().getOnlinePlayers().stream().filter(staff -> plugin.getGameProfile(staff.getUniqueId()).hasRank(Rank.HELPER)).forEach(staff ->
                staff.sendMessage(ChatColor.RED + player.getName() + " unmuted " + targetData.getName()));

        Player target = Bukkit.getPlayer(targetData.getUuid());
        if (target != null) {
            target.sendMessage(ChatColor.RED + " \nYou were unmuted by " + ChatColor.GOLD + player.getName());
            target.sendMessage(ChatColor.GRAY + p.getReason().getMessage() + "\n ");
        }

        return true;
    }

}
