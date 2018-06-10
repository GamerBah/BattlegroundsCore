package com.battlegroundspvp.administration.command;
/* Created by GamerBah on 10/12/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.util.enums.EventSound;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CrateCommand implements CommandExecutor {

    @Getter
    private static List<Player> adding = new ArrayList<>();
    @Getter
    private static List<Player> removing = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player))
            return true;

        Player player = (Player) sender;
        GameProfile gameProfile = BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId());

        if (!gameProfile.hasRank(Rank.OWNER)) {
            BattlegroundsCore.getInstance().sendNoPermission(player);
            return true;
        }

        if (args.length != 1) {
            BattlegroundsCore.getInstance().sendIncorrectUsage(player, "/crate <add/remove>");
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (adding.contains(player)) {
                player.sendMessage(ChatColor.RED + "You are already trying to add a crate!");
                player.sendMessage(ChatColor.YELLOW + "Place an Enderchest onto an End Portal Frame to add a BattleCrate location...");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }
            adding.add(player);
            player.sendMessage(ChatColor.YELLOW + "Place an Enderchest onto an End Portal Frame to add a BattleCrate location...");
            EventSound.playSound(player, EventSound.CLICK);
            return true;

        } else if (args[0].equalsIgnoreCase("remove")) {
            if (removing.contains(player)) {
                player.sendMessage(ChatColor.RED + "You are already trying to remove a crate!");
                player.sendMessage(ChatColor.YELLOW + "Break an Enderchest that's on an End Portal Frame to remove a BattleCrate location...");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }
            removing.add(player);
            player.sendMessage(ChatColor.YELLOW + "Break an Enderchest that's on an End Portal Frame to remove a BattleCrate location...");
            EventSound.playSound(player, EventSound.CLICK);
            return true;
        } else {
            BattlegroundsCore.getInstance().sendIncorrectUsage(player, "/crate <add/remove>");
            return false;
        }
    }
}
