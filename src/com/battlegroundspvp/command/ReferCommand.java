package com.battlegroundspvp.command;
/* Created by GamerBah on 9/7/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReferCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public ReferCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (args.length != 1) {
            plugin.sendIncorrectUsage(player, "/refer <username>");
            return true;
        }

        if (gameProfile.getRecruitedBy() != -1) {
            player.sendMessage(ChatColor.RED + "You've already referred someone!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (args[0].equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "You can't refer yourself!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (plugin.getGameProfile(args[0]) == null) {
            player.sendMessage(ChatColor.RED + "That player doesn't exist!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        GameProfile recruitData = plugin.getGameProfile(args[0]).addPlayerRecruited();
        Player recruiter = plugin.getServer().getPlayer(args[0]);
        if (recruiter != null) {
            recruiter.sendMessage(ChatColor.GREEN + "Successfully recruited " + player.getName() + "! " + ChatColor.AQUA + "[+75 Souls]" + ChatColor.LIGHT_PURPLE + "[+50 Battle Coins]");
            EventSound.playSound(recruiter, EventSound.ACTION_SUCCESS);
        }
        gameProfile.setRecruitedBy(recruitData.getId());
        player.sendMessage(ChatColor.GREEN + "Successfully referred " + recruitData.getName() + "! " + ChatColor.AQUA + "[+25 Souls]" + ChatColor.LIGHT_PURPLE + "[+10 Battle Coins]");
        EventSound.playSound(player, EventSound.ACTION_SUCCESS);

        return false;
    }

}