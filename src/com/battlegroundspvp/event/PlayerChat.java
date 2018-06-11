package com.battlegroundspvp.event;
/* Created by GamerBah on 8/9/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.command.ChatCommands;
import com.battlegroundspvp.administration.command.StaffChatCommand;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.command.MessageCommand;
import com.battlegroundspvp.runnable.timer.AFKRunnable;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.message.ChatFilter;
import com.battlegroundspvp.util.message.MessageBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Pattern;

public class PlayerChat implements Listener {

    private BattlegroundsCore plugin;

    public PlayerChat(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);

        if (StaffChatCommand.getToggled().contains(player.getUniqueId())) {
            plugin.getServer().getOnlinePlayers().stream().filter(players ->
                    plugin.getGameProfile(players.getUniqueId()).hasRank(Rank.HELPER))
                    .forEach(players -> players.sendMessage(new MessageBuilder(ChatColor.YELLOW).bold().create() + "[STAFF] "
                            + ChatColor.RED + player.getName() + ": " + event.getMessage()));
            return;
        }

        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (gameProfile.isMuted()) {
            MessageCommand.sendErrorMessage(gameProfile);
            return;
        }

        Rank rank = gameProfile.getRank();

        if (AFKRunnable.getAfkTimer().containsKey(player))
            AFKRunnable.getAfkTimer().put(player, 0);


        if (ChatCommands.chatSilenced && !gameProfile.hasRank(Rank.HELPER))
            return;

        boolean hasRank = gameProfile.hasRank(Rank.WARRIOR);

        if (!ChatFilter.isClean(event.getMessage())) {
            event.setCancelled(true);
            player.sendMessage(new MessageBuilder(ChatColor.RED).bold().create() + "Please refrain from using profane language!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            if (!ChatFilter.getAttempts().containsKey(player)) {
                if (!plugin.getGameProfile(player.getUniqueId()).hasRank(Rank.HELPER)) {
                    ChatFilter.getAttempts().put(player, 1);
                }
            } else {
                ChatFilter.getAttempts().put(player, ChatFilter.getAttempts().get(player) + 1);
                if (ChatFilter.getAttempts().get(player) == 10) {
                    ChatFilter.getAttempts().remove(player);
                    //plugin.warnPlayer(null, gameProfile, Punishment.Reason.ATTEMPT_SWEARING);
                }
            }
            return;
        }

        plugin.getServer().getOnlinePlayers().forEach(p -> {
            if (Pattern.compile(Pattern.quote("@" + p.getName()), Pattern.CASE_INSENSITIVE).matcher(event.getMessage()).find()) {
                p.sendMessage(rank.getColor().create() + (hasRank ? rank.getName().toUpperCase() + ChatColor.RESET : "")
                        + " " + player.getName() + ChatColor.GRAY + " \u00BB " + (hasRank ? ChatColor.WHITE : ChatColor.GRAY)
                        + event.getMessage().replaceAll("@(?i)" + p.getName(), ChatColor.AQUA + "@" + p.getName() + (hasRank ? ChatColor.WHITE : ChatColor.GRAY)));
                EventSound.playSound(p, EventSound.CHAT_TAGGED);
            } else {
                p.sendMessage(rank.getColor().create() + (hasRank ? rank.getName().toUpperCase() + ChatColor.RESET : "")
                        + " " + player.getName() + ChatColor.GRAY + " \u00BB " + (hasRank ? ChatColor.WHITE : ChatColor.GRAY) + event.getMessage());
            }
        });
        Bukkit.getLogger().info(rank.getColor().create() + (hasRank ? rank.getName().toUpperCase() + ChatColor.RESET : "")
                + " " + player.getName() + ChatColor.GRAY + " \u00BB " + (hasRank ? ChatColor.WHITE : ChatColor.GRAY)
                + event.getMessage());
    }

}
