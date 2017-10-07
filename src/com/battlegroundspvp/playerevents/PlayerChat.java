package com.battlegroundspvp.playerevents;
/* Created by GamerBah on 8/9/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.commands.ChatCommands;
import com.battlegroundspvp.administration.commands.StaffChatCommand;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.runnables.AFKRunnable;
import com.battlegroundspvp.utils.enums.ColorBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener {

    private BattlegroundsCore plugin;

    public PlayerChat(BattlegroundsCore plugin) {
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

        //if (gameProfile.isMuted(event)) return;

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
