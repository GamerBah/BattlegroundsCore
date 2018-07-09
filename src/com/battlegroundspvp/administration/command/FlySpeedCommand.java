package com.battlegroundspvp.administration.command;
/* Created by GamerBah on 8/10/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.battlegroundspvp.util.message.MessageBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlySpeedCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public FlySpeedCommand(BattlegroundsCore plugin) {
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
            if (!gameProfile.hasRank(Rank.ADMIN)) {
                plugin.sendNoPermission(player);
                return true;
            }

            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "/flyspeed <speed>");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            if (!args[0].matches("[0-9]+")) {
                player.sendMessage(ChatColor.RED + "Please select a number from 1 to 10!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            if (Integer.parseInt(args[0]) > 10) {
                player.sendMessage(ChatColor.RED + "Please select a number from 1 to 10!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            float speed = 0f;
            switch (args[0]) {
                case "1":
                    speed = 0.1f;
                    break;
                case "2":
                    speed = 0.2f;
                    break;
                case "3":
                    speed = 0.3f;
                    break;
                case "4":
                    speed = 0.4f;
                    break;
                case "5":
                    speed = 0.5f;
                    break;
                case "6":
                    speed = 0.6f;
                    break;
                case "7":
                    speed = 0.7f;
                    break;
                case "8":
                    speed = 0.8f;
                    break;
                case "9":
                    speed = 0.9f;
                    break;
                case "10":
                    speed = 1.0f;
                    break;
            }
            player.setFlySpeed(speed);
            player.sendMessage(new MessageBuilder(ChatColor.GREEN).bold().create() + "Success! " + ChatColor.GRAY + "Your fly speed was set to " + new MessageBuilder(ChatColor.GREEN).bold().create() + args[0]);
            EventSound.playSound(player, EventSound.ACTION_SUCCESS);
            return true;
        }
        return false;
    }

}