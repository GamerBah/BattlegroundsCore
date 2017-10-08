package com.battlegroundspvp.commands;
/* Created by GamerBah on 8/7/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.enums.ColorBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RulesCommand implements CommandExecutor {
    private BattlegroundsCore plugin;

    public RulesCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§m---------------§f[ " + new ColorBuilder(ChatColor.RED).bold().create() + "SERVER RULES " + ChatColor.WHITE + "]§m---------------");
            player.sendMessage(new ColorBuilder(ChatColor.GOLD).bold().create() + " 1. " + ChatColor.YELLOW + "Be respectful to everyone");
            player.sendMessage(new ColorBuilder(ChatColor.GOLD).bold().create() + " 2. " + ChatColor.YELLOW + "Play fair (no hacked clients or use of glitches)");
            player.sendMessage(ChatColor.GRAY + "    - If you see someone hacking or using a glitch, report them!");
            player.sendMessage(new ColorBuilder(ChatColor.GOLD).bold().create() + " 3. " + ChatColor.YELLOW + "Do not pretend to be a Staff Member");
            player.sendMessage(ChatColor.GRAY + "    - Let the Staff Members do their jobs");
            player.sendMessage(new ColorBuilder(ChatColor.GOLD).bold().create() + " 4. " + ChatColor.YELLOW + "No racist, sexual, or other offensive types of remarks");
            player.sendMessage(new ColorBuilder(ChatColor.GOLD).bold().create() + " 5. " + ChatColor.YELLOW + "Do not ask about an application you sent in");
            player.sendMessage(new ColorBuilder(ChatColor.GOLD).bold().create() + " 7. " + ChatColor.YELLOW + "Your account = Your responsibility!");
            player.sendMessage(ChatColor.GRAY + "    - If it's a shared account, we will still punish accordingly!");
            player.sendMessage(new ColorBuilder(ChatColor.GOLD).bold().create() + " 9. " + ChatColor.YELLOW + "Ban evading will result in an IP-Ban!");
            player.sendMessage(new ColorBuilder(ChatColor.GOLD).bold().create() + " 10. " + ChatColor.YELLOW + "Do not advertise for other servers");
            player.sendMessage("\n" + ChatColor.GRAY + "More info about these rules, as well as more detailed rules,");
            player.sendMessage(ChatColor.GRAY + "can be found on the forums! battlegroundspvp.com");
            // Send Other Rules from plugin instances
        }

        return false;
    }
}
