package com.battlegroundspvp.administration.command;
/* Created by GamerBah on 8/26/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.battlegroundspvp.util.manager.UpdateManager;
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
        GameProfile gameProfile = GameProfileManager.getGameProfile(player.getUniqueId());

        if (gameProfile != null) {
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
                UpdateManager.enterMaintenance();
            } else {
                UpdateManager.setDevelopmentMode(false);
                for (Player players : plugin.getServer().getOnlinePlayers()) {
                    players.sendMessage(new MessageBuilder(ChatColor.RED).bold().create() + "\nSERVER IS NO LONGER IN " + new MessageBuilder(ChatColor.GOLD).bold().create() + "MAINTENANCE MODE\n ");
                    EventSound.playSound(players, EventSound.ACTION_SUCCESS);
                }
            }
            return true;
        }
        return false;
    }

}