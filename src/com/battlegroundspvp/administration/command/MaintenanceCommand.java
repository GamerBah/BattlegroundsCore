package com.battlegroundspvp.administration.command;
/* Created by GamerBah on 8/26/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.message.MessageBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MaintenanceCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public MaintenanceCommand(BattlegroundsCore plugin) {
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
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Code required.");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (!args[0].equals("GamerBahxxRiteTurnOnlyxxAvoriz")) {
            player.sendMessage(ChatColor.RED + "Incorrect code.");
            return true;
        }

        if (!plugin.getConfig().getBoolean("developmentMode")) {
            for (Player players : plugin.getServer().getOnlinePlayers()) {
                GameProfile gameProfile1 = plugin.getGameProfile(players.getUniqueId());
                if (!gameProfile1.hasRank(Rank.HELPER)) {
                    players.kickPlayer(ChatColor.RED + "You were kicked because the server was put into\n" + new MessageBuilder(ChatColor.GOLD).bold().create()
                            + "MAINTENANCE MODE\n\n" + ChatColor.AQUA + "This means that we are fixing bugs, or found another issue we needed to take care of\n\n"
                            + ChatColor.GRAY + "We put the server into Maintenance Mode in order to reduce the risk of\n§7corrupting player data, etc. The server should be open shortly!");
                }
                EventSound.playSound(players, EventSound.ACTION_SUCCESS);
            }
            plugin.getConfig().set("developmentMode", true);
            plugin.saveConfig();
            plugin.getServer().broadcastMessage(new MessageBuilder(ChatColor.RED).bold().create() + "\nSERVER HAS BEEN PUT INTO " + new MessageBuilder(ChatColor.GOLD).bold().create() + "MAINTENANCE MODE\n ");
        } else {
            plugin.getConfig().set("developmentMode", false);
            plugin.saveConfig();
            for (Player players : plugin.getServer().getOnlinePlayers()) {
                players.sendMessage(new MessageBuilder(ChatColor.RED).bold().create() + "\nSERVER IS NO LONGER IN " + new MessageBuilder(ChatColor.GOLD).bold().create() + "MAINTENANCE MODE\n ");
                EventSound.playSound(players, EventSound.ACTION_SUCCESS);
            }
        }

        return false;
    }

}