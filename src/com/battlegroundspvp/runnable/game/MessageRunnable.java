package com.battlegroundspvp.runnable.game;
/* Created by GamerBah on 12/8/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.command.ChatCommands;
import com.battlegroundspvp.util.message.MessageBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageRunnable implements Runnable {

    private BattlegroundsCore plugin;
    private int amount = 0;

    public MessageRunnable(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(file);
        if (messageConfig.getStringList("Messages") != null) {
            if (amount >= messageConfig.getStringList("Messages").size()) {
                amount = 0;
            }

            if (!ChatCommands.chatSilenced && plugin.getServer().getOnlinePlayers().size() > 1) {
                plugin.getServer().broadcastMessage(" ");
                plugin.getServer().broadcastMessage(ChatColor.RED + "[" + new MessageBuilder(ChatColor.GOLD).bold().create() + "*" + ChatColor.RED + "] "
                        + ChatColor.YELLOW + ChatColor.translateAlternateColorCodes('&',
                        messageConfig.getStringList("Messages").get(amount++)));
                plugin.getServer().broadcastMessage(" ");
            }
        }
    }
}
