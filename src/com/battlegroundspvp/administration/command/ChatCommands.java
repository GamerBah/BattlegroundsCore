package com.battlegroundspvp.administration.command;

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.battlegroundspvp.util.message.MessageBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommands implements CommandExecutor {

    public static boolean chatSilenced = false;
    private BattlegroundsCore plugin;

    public ChatCommands(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = GameProfileManager.getGameProfile(player.getUniqueId());

        if (gameProfile != null) {
            if (cmd.getName().equalsIgnoreCase("clearchat")) {
                if (!gameProfile.hasRank(Rank.MODERATOR)) {
                    plugin.sendNoPermission(player);
                    return true;
                } else {
                    for (int i = 0; i <= 100; i++) {
                        for (Player players : plugin.getServer().getOnlinePlayers()) {
                            players.sendMessage(" ");
                        }
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(new MessageBuilder(ChatColor.RED).bold().create() + "The chat has been cleared by " + player.getName() + "!");
                        p.sendMessage(" ");
                        EventSound.playSound(p, EventSound.ACTION_SUCCESS);
                    }
                }
            }

            if (cmd.getName().equalsIgnoreCase("lockchat")) {
                if (!gameProfile.hasRank(Rank.MODERATOR)) {
                    plugin.sendNoPermission(player);
                    return true;
                } else {
                    if (!chatSilenced) {
                        chatSilenced = true;
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.sendMessage(" ");
                            p.sendMessage(new MessageBuilder(ChatColor.RED).bold().create() + "The chat has been locked by " + player.getName() + "!");
                            p.sendMessage(" ");
                            EventSound.playSound(p, EventSound.ACTION_SUCCESS);
                        }
                    } else {
                        chatSilenced = false;
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.sendMessage(" ");
                            p.sendMessage(new MessageBuilder(ChatColor.RED).bold().create() + "All chat has been re-enabled!");
                            p.sendMessage(" ");
                            EventSound.playSound(p, EventSound.ACTION_SUCCESS);
                        }
                    }
                }
            }

            return true;
        }
        return false;
    }

}
