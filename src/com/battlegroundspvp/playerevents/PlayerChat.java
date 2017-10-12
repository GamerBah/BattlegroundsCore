package com.battlegroundspvp.playerevents;
/* Created by GamerBah on 8/9/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.commands.ChatCommands;
import com.battlegroundspvp.administration.commands.StaffChatCommand;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.commands.MessageCommand;
import com.battlegroundspvp.runnables.AFKRunnable;
import com.battlegroundspvp.utils.ColorBuilder;
import com.battlegroundspvp.utils.enums.EventSound;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Pattern;

public class PlayerChat implements Listener {

    private BattlegroundsCore plugin;

    public PlayerChat(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(true);

        if (StaffChatCommand.getToggled().contains(player.getUniqueId())) {
            plugin.getServer().getOnlinePlayers().stream().filter(players ->
                    plugin.getGameProfile(players.getUniqueId()).hasRank(Rank.HELPER))
                    .forEach(players -> players.sendMessage(new ColorBuilder(ChatColor.YELLOW).bold().create() + "[STAFF] "
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
