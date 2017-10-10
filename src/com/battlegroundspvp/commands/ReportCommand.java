package com.battlegroundspvp.commands;

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.utils.enums.EventSound;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ReportCommand implements CommandExecutor {

    @Getter
    private static HashMap<UUID, Integer> cooldown = new HashMap<>();
    @Getter
    private static HashMap<UUID, String> reportBuilders = new HashMap<>();
    @Getter
    private static HashMap<UUID, ArrayList<String>> reportArray = new HashMap<>();
    private BattlegroundsCore plugin;

    public ReportCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId());

        if (gameProfile.isMuted()) {
            MessageCommand.sendErrorMessage(gameProfile);
            return true;
        }

        if (args.length != 1) {
            plugin.sendIncorrectUsage(player, "/report <player>");
            return true;
        }

        Player reported = plugin.getServer().getPlayerExact(args[0]);

        if (reported == null) {
            player.sendMessage(ChatColor.RED + "That player isn't online!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (reported == player) {
            player.sendMessage(ChatColor.RED + "You can't report yourself! Unless you have something to tell us.... *gives suspicious look*");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (!cooldown.containsKey(player.getUniqueId())) {
            //ReportMenu reportMenu = new ReportMenu(player);
            reportBuilders.put(player.getUniqueId(), null);
            reportArray.put(player.getUniqueId(), new ArrayList<>());
            //reportMenu.openInventory(player, reported);
        } else {
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            player.sendMessage(ChatColor.RED + "You must wait " + ChatColor.YELLOW
                    + cooldown.get(player.getUniqueId()) + " seconds " + ChatColor.RED + "before you report another player!");
            return false;
        }
        return false;
    }
}
