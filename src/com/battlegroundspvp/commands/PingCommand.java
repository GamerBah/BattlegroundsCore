package com.battlegroundspvp.commands;
/* Created by GamerBah on 8/15/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.ColorBuilder;
import com.battlegroundspvp.utils.enums.EventSound;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {
    private BattlegroundsCore plugin;

    public PingCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        String nmsVersion = plugin.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1);

        Player target = player;

        if (args.length == 1) {
            target = plugin.getServer().getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(ChatColor.RED + "That player isn't online!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }
        } else if (args.length > 2) {
            plugin.sendIncorrectUsage(player, "/ping [player]");
            return true;
        }

        int ping = -1;

        try {
            Object nmsPlayer = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer").cast(player).getClass().getMethod("getHandle").invoke(target);
            ping = nmsPlayer.getClass().getField("ping").getInt(nmsPlayer);
        } catch (Exception e) {
            plugin.getLogger().severe("Uh oh! Unable to retrieve the player's ping!");
            e.printStackTrace();
        }

        String status = "";
        if (ping <= 20) {
            status = new ColorBuilder(ChatColor.LIGHT_PURPLE).bold().create() + "AWESOME! ";
        }
        if (ping > 20 && ping <= 50) {
            status = new ColorBuilder(ChatColor.DARK_PURPLE).bold().create() + "GREAT! ";
        }
        if (ping > 50 && ping <= 80) {
            status = new ColorBuilder(ChatColor.GREEN).bold().create() + "Good! ";
        }
        if (ping > 80 && ping <= 110) {
            status = new ColorBuilder(ChatColor.DARK_GREEN).bold().create() + "Okay. ";
        }
        if (ping > 110 && ping <= 140) {
            status = new ColorBuilder(ChatColor.RED).bold().create() + "Eh... ";
        }
        if (ping > 140 && ping <= 200) {
            status = new ColorBuilder(ChatColor.RED).bold().create() + "Bad. ";
        }
        if (ping > 200 && ping <= 275) {
            status = new ColorBuilder(ChatColor.RED).bold().create() + "AWFUL! ";
        }
        if (ping > 275) {
            status = new ColorBuilder(ChatColor.RED).bold().create() + "RIP. ";
        }

        if (player == target) {
            player.sendMessage(ChatColor.GRAY + "Your connection to Battlegrounds is " + status + ChatColor.GRAY + "(" + ping + "ms)");
        } else {
            player.sendMessage(ChatColor.RED + target.getName() + ChatColor.GRAY + "'s connection to BattlegroundsCore is " + status + ChatColor.GRAY + "(" + ping + "ms)");
        }

        return true;
    }

}
