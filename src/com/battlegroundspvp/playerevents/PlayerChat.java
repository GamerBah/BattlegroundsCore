package com.battlegroundspvp.playerevents;
/* Created by GamerBah on 8/9/2016 */

import com.battlegroundspvp.Core;
import com.battlegroundspvp.administration.commands.ChatCommands;
import com.battlegroundspvp.administration.commands.StaffChatCommand;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.runnables.AFKRunnable;
import com.battlegroundspvp.utils.enums.ColorBuilder;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.enums.Time;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;

public class PlayerChat implements Listener {

    private Core plugin;

    public PlayerChat(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (StaffChatCommand.getToggled().contains(player.getUniqueId())) {
            event.setCancelled(true);
            plugin.getServer().getOnlinePlayers().stream().filter(players ->
                    plugin.getGameProfile(players.getUniqueId()).hasRank(Rank.HELPER))
                    .forEach(players -> players.sendMessage(ColorBuilder.YELLOW.bold().create() + "[STAFF] "
                            + ChatColor.RED + player.getName() + ": " + event.getMessage()));
            return;
        }

        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (plugin.getPlayerPunishments().containsKey(player.getUniqueId())) {
            ArrayList<Punishment> punishments = plugin.getPlayerPunishments().get(player.getUniqueId());
            for (int i = 0; i < punishments.size(); i++) {
                Punishment punishment = punishments.get(i);
                if (!punishment.isPardoned()) {
                    event.setCancelled(true);
                    BaseComponent baseComponent = new TextComponent(ChatColor.RED + "You are muted! " + ChatColor.GRAY + "(Hover to view details)");
                    baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Muted by: "
                            + ChatColor.WHITE + plugin.getServer().getPlayer(punishment.getEnforcer()).getName() + "\n" + ChatColor.GRAY + "Reason: "
                            + ChatColor.WHITE + punishment.getReason().getName() + "\n" + ChatColor.GRAY + "Time Remaining: " + ChatColor.WHITE +
                            Time.toString(Time.punishmentTimeRemaining(punishment.getExpiration()), true)).create()));
                    player.spigot().sendMessage(baseComponent);
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    return;
                }
            }
        }

        Rank rank = gameProfile.getRank();

        if (AFKRunnable.getAfkTimer().containsKey(player)) {
            AFKRunnable.getAfkTimer().put(player, 0);
        }

        if (gameProfile.hasRank(Rank.WARRIOR)) {
            ChatColor color = rank.getColor();
            event.setFormat((color == null ? " " : color + " ") + ChatColor.BOLD + rank.getName().toUpperCase() + ChatColor.RESET + " %s" + ChatColor.GRAY + " \u00BB " + ChatColor.WHITE + "%s");
        } else {
            event.setFormat(ChatColor.GRAY + " %s" + ChatColor.GRAY + " \u00BB " + ChatColor.GRAY + "%s");
        }

        if (ChatCommands.chatSilenced && !gameProfile.hasRank(Rank.HELPER)) {
            event.setCancelled(true);
        }
    }

}
