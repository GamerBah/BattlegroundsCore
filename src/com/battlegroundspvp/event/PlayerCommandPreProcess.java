package com.battlegroundspvp.event;
/* Created by GamerBah on 8/15/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.command.FreezeCommand;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.runnable.timer.AFKRunnable;
import com.battlegroundspvp.util.UpdateManager;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import de.Herbystar.TTA.TTA_Methods;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.potion.PotionEffectType;

public class PlayerCommandPreProcess implements Listener {
    private BattlegroundsCore plugin;

    public PlayerCommandPreProcess(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());
        String command = event.getMessage();

        if (UpdateManager.isUpdating() || FreezeCommand.reloadFreeze) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "No commands are available during an update!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return;
        }

        if (StringUtils.startsWithIgnoreCase(command, "/me")
                || StringUtils.startsWithIgnoreCase(command, "/minecraft:")
                || StringUtils.startsWithIgnoreCase(command, "/bukkit:")
                || StringUtils.startsWithIgnoreCase(command, "/spigot:")
                || StringUtils.startsWithIgnoreCase(command, "/battlegroundscore:")) {
            event.setCancelled(true);
            return;
        }

        if (StringUtils.equalsIgnoreCase(command, "/help")) {
            event.setCancelled(true);
            player.performCommand("battlegroundscore:help");
            return;
        }
        if (StringUtils.equalsIgnoreCase(command, "/help staff")) {
            event.setCancelled(true);
            player.performCommand("battlegroundscore:help staff");
            return;
        }
        if (StringUtils.equalsIgnoreCase(command, "/reload")) {
            event.setCancelled(true);
            player.performCommand("battlegroundscore:reload");
            return;
        }
        if (StringUtils.equalsIgnoreCase(command, "/reload server")) {
            event.setCancelled(true);
            player.performCommand("battlegroundscore:reload server");
            return;
        }
        if (StringUtils.equalsIgnoreCase(command, "/reload messages")) {
            event.setCancelled(true);
            player.performCommand("battlegroundscore:reload messages");
            return;
        }
        if (StringUtils.equalsIgnoreCase(command, "/reload plugin")) {
            event.setCancelled(true);
            player.performCommand("battlegroundscore:reload plugin");
            return;
        }
        if (StringUtils.contains(command, "/kick")) {
            event.setCancelled(true);
            if (command.length() > 6) {
                player.performCommand("battlegroundscore:kick " + command.substring(6, command.length()));
            } else {
                player.performCommand("battlegroundscore:kick");
            }
            return;
        }
        if (StringUtils.equalsIgnoreCase(command, "/setworldspawn")) {
            event.setCancelled(true);
            player.performCommand("battlegroundscore:setspawn");
            return;
        }
        if (StringUtils.equalsIgnoreCase(command, "/npc")) {
            event.setCancelled(true);
            player.performCommand("battlegroundscore:npc");
            return;
        }
        if (StringUtils.equalsIgnoreCase(command, "/npc add")) {
            event.setCancelled(true);
            player.performCommand("battlegroundscore:npc add");
            return;
        }
        if (StringUtils.equalsIgnoreCase(command, "/npc remove")) {
            event.setCancelled(true);
            player.performCommand("battlegroundscore:npc remove");
            return;
        }
        if (StringUtils.equalsIgnoreCase(command, "/npc cancel")) {
            event.setCancelled(true);
            player.performCommand("battlegroundscore:npc cancel");
            return;
        }

        if (BattlegroundsCore.getAfk().contains(player.getUniqueId()) && !StringUtils.startsWithIgnoreCase(command, "/afk") && !StringUtils.startsWithIgnoreCase(command, "/spawn")) {
            BattlegroundsCore.getAfk().remove(player.getUniqueId());
            player.sendMessage(ChatColor.GRAY + "You are no longer AFK");
            EventSound.playSound(player, EventSound.CLICK);
            TTA_Methods.sendTitle(player, null, 0, 0, 0, null, 0, 0, 0);
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            plugin.respawn(player);
        }

        if (AFKRunnable.getAfkTimer().containsKey(player)) {
            AFKRunnable.getAfkTimer().put(player, 0);
        }

        plugin.getServer().getOnlinePlayers().stream().filter(players ->
                plugin.getGameProfile(players.getUniqueId()).hasRank(Rank.HELPER) && BattlegroundsCore.getCmdspies().contains(players.getUniqueId()))
                .forEach(players -> players.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "" + ChatColor.DARK_GRAY + player.getName() + ": " + ChatColor.GRAY + command));
    }

}
