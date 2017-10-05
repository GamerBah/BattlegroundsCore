package com.battlegroundspvp.commands;
/* Created by GamerBah on 3/6/2016 */

import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.Core;
import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.utils.enums.Time;
import net.gpedro.integrations.slack.SlackMessage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class StaffReqCommand implements CommandExecutor {

    private Core plugin;
    private HashMap<UUID, Integer> cooldown = new HashMap<>();

    public StaffReqCommand(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (plugin.getPlayerPunishments().containsKey(player.getUniqueId())) {
            ArrayList<Punishment> punishments = plugin.getPlayerPunishments().get(player.getUniqueId());
            for (int i = 0; i < punishments.size(); i++) {
                Punishment punishment = punishments.get(i);
                if (!punishment.isPardoned()) {
                    BaseComponent baseComponent = new TextComponent(ChatColor.RED + "You are muted! " + ChatColor.GRAY + "(Hover to view details)");
                    baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Muted by: "
                            + ChatColor.WHITE + plugin.getServer().getPlayer(punishment.getEnforcer()).getName() + "\n" + ChatColor.GRAY + "Reason: "
                            + ChatColor.WHITE + punishment.getReason().getName() + "\n" + ChatColor.GRAY + "Time Remaining: " + ChatColor.WHITE +
                            Time.toString(Time.punishmentTimeRemaining(punishment.getExpiration()), true)).create()));
                    player.spigot().sendMessage(baseComponent);
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    return true;
                }
            }
        }

        if (gameProfile.hasRank(Rank.HELPER)) {
            player.sendMessage(ChatColor.RED + "You need help from a Staff member? You are a Staff member, silly! Use Slack! xD");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/" + label + " <message>");
            return true;
        }

        if (cooldown.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must wait " + cooldown.get(player.getUniqueId()) + " seconds until you can use this command again!");
            return true;
        }

        String message = StringUtils.join(args, ' ', 0, args.length);

        plugin.slackStaffRequests.call(new SlackMessage(">>>*" + player.getName() + ":* _" + message + "_"));

        player.sendMessage(ChatColor.GREEN + "Your message has been sent! A staff member has been notified of your request and should be able to help you shortly!");
        EventSound.playSound(player, EventSound.ACTION_SUCCESS);
        cooldown.put(player.getUniqueId(), 60);
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (cooldown.containsKey(player.getUniqueId())) {
                if (cooldown.get(player.getUniqueId()) >= 0) {
                    cooldown.put(player.getUniqueId(), (cooldown.get(player.getUniqueId()) - 1));
                } else {
                    cooldown.remove(player.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "You can now use " + ChatColor.YELLOW + "/staffreq " + ChatColor.GREEN + "again!");
                }
            }
        }, 1L, 20L);

        return false;
    }
}
