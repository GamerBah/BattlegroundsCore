package com.battlegroundspvp.command;
/* Created by GamerBah on 8/31/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.util.message.MessageBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public HelpCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (args.length > 1) {
            plugin.sendIncorrectUsage(player, "/help" + (gameProfile.hasRank(Rank.HELPER) ? " [\"staff\"]" : ""));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§m------------------§f[ " + ChatColor.RED + "Command Help " + ChatColor.WHITE + "]§m------------------");
            if (gameProfile.hasRank(Rank.HELPER))
                player.sendMessage(ChatColor.GOLD + " You can use §e/help staff §6for Staff-related commands");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /afk " + ChatColor.GRAY + "- Sets you as away-from-keyboard");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /dailyreward " + ChatColor.GRAY + "- Claims an available Daily Reward");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /essences " + ChatColor.GRAY + "- Shows you your purchased Battle Essences");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /friend " + ChatColor.GRAY + "- Add, remove, and view friends!");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /message " + ChatColor.GRAY + "- Sends a private message to a player");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /options " + ChatColor.GRAY + "- Shows options for a specified player");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /ping " + ChatColor.GRAY + "- Shows you your connection to Battlegrounds");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /refer " + ChatColor.GRAY + "- Refer the player who invited you!");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /reply " + ChatColor.GRAY + "- Replies to the most recent private message");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /report " + ChatColor.GRAY + "- Reports a player to Staff members");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /rules " + ChatColor.GRAY + "- Displays the server rules");
            if (gameProfile.hasRank(Rank.WARRIOR))
                player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /spectate " + ChatColor.GRAY + "- Puts you into spectator mode");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /spawn " + ChatColor.GRAY + "- Teleports you back to the spawn");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /staffreq " + ChatColor.GRAY + "- Sends a message to offline Staff members");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /team " + ChatColor.GRAY + "- Team up with another player");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /thanks " + ChatColor.GRAY + "- Thanks the player with the active Battle Essence");
            return true;
        }

        if (args[0].equalsIgnoreCase("staff")) {
            if (!gameProfile.hasRank(Rank.HELPER)) {
                plugin.sendNoPermission(player);
                return true;
            }
            player.sendMessage("§m---------------§f[ " + ChatColor.RED + "Staff Command Help " + ChatColor.WHITE + "]§m---------------");
            if (gameProfile.hasRank(Rank.ADMIN))
                player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /ban " + ChatColor.GRAY + "- Permanently bans a player");
            if (gameProfile.hasRank(Rank.MODERATOR))
                player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /clearchat " + ChatColor.GRAY + "- Clears the chat");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /cmdspy " + ChatColor.GRAY + "- Allows you to see all executed commands");
            if (gameProfile.hasRank(Rank.MODERATOR))
                player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /flyspeed " + ChatColor.GRAY + "- Changes your flying speed");
            if (gameProfile.hasRank(Rank.MODERATOR))
                player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /freeze " + ChatColor.GRAY + "- Freezes players");
            if (gameProfile.hasRank(Rank.MODERATOR))
                player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /lockchat " + ChatColor.GRAY + "- Locks the chat");
            if (gameProfile.hasRank(Rank.OWNER))
                player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /maintenance " + ChatColor.GRAY + "- Puts the server into Maintenance Mode");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /mute " + ChatColor.GRAY + "- Mutes a player");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /kick " + ChatColor.GRAY + "- Kicks a player from the server");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /punish " + ChatColor.GRAY + "- Views a player's punishment history");
            if (gameProfile.hasRank(Rank.MODERATOR))
                player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /skull " + ChatColor.GRAY + "- Gets a players head");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /staff " + ChatColor.GRAY + "- Sends a message to the Staff chat channel");
            if (gameProfile.hasRank(Rank.MODERATOR))
                player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /temp-ban " + ChatColor.GRAY + "- Bans a player temporarily");
            if (gameProfile.hasRank(Rank.MODERATOR))
                player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /unban " + ChatColor.GRAY + "- Unbans a player");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /unmute " + ChatColor.GRAY + "- unmutes a player");
            player.sendMessage(new MessageBuilder(ChatColor.DARK_AQUA).bold().create() + " /warn " + ChatColor.GRAY + "- warns a player");

            return true;
        }

        return false;
    }

}