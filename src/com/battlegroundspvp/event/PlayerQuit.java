package com.battlegroundspvp.event;
/* Created by GamerBah on 8/7/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.command.ReportCommand;
import com.battlegroundspvp.runnable.timer.DonationUpdater;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.battlegroundspvp.util.message.MessageBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.LocalDateTime;

public class PlayerQuit implements Listener {
    private BattlegroundsCore plugin;

    public PlayerQuit(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameProfile gameProfile = GameProfileManager.getGameProfile(player.getUniqueId());

        if (gameProfile != null) {
        /*
        if (SpectateCommand.getSpectating().contains(player)) {
            SpectateCommand.getSpectating().remove(player);
        }
        */

            ReportCommand.getReportBuilders().remove(player.getUniqueId());

            ReportCommand.getReportArray().remove(player.getUniqueId());

            BattlegroundsCore.getAfk().remove(player.getUniqueId());

            if (gameProfile.getPlayerSettings().isStealthyJoin()) {
                event.setQuitMessage(null);
                MessageBuilder.sendStaffMessage(new MessageBuilder(ChatColor.DARK_GRAY).bold().create() + "[" + new MessageBuilder(ChatColor.RED).bold().create() + "-"
                        + new MessageBuilder(ChatColor.DARK_GRAY).bold().create() + "] " + ChatColor.WHITE + event.getPlayer().getName());
            } else {
                event.setQuitMessage(new MessageBuilder(ChatColor.DARK_GRAY).bold().create() + "[" + new MessageBuilder(ChatColor.RED).bold().create() + "-"
                        + new MessageBuilder(ChatColor.DARK_GRAY).bold().create() + "] " + ChatColor.WHITE + event.getPlayer().getName());
            }

            gameProfile.setLastOnline(LocalDateTime.now());

            if (DonationUpdater.essenceBar != null)
                if (DonationUpdater.essenceBar.getPlayers().contains(player))
                    DonationUpdater.essenceBar.removePlayer(player);

            plugin.getAwaitingRegistration().remove(player);

            gameProfile.fullSync();
            GameProfileManager.getGameProfiles().remove(gameProfile);
        }
    }
}
