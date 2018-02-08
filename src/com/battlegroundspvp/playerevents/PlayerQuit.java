package com.battlegroundspvp.playerevents;
/* Created by GamerBah on 8/7/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.commands.ReportCommand;
import com.battlegroundspvp.runnables.DonationUpdater;
import com.battlegroundspvp.utils.messages.ColorBuilder;
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
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        /*
        if (SpectateCommand.getSpectating().contains(player)) {
            SpectateCommand.getSpectating().remove(player);
        }
        */

        if (ReportCommand.getReportBuilders().containsKey(player.getUniqueId())) {
            ReportCommand.getReportBuilders().remove(player.getUniqueId());
        }

        if (ReportCommand.getReportArray().containsKey(player.getUniqueId())) {
            ReportCommand.getReportArray().remove(player.getUniqueId());
        }

        if (BattlegroundsCore.getAfk().contains(player.getUniqueId())) {
            BattlegroundsCore.getAfk().remove(player.getUniqueId());
        }

        if (gameProfile.getPlayerSettings().isStealthyJoin()) {
            event.setQuitMessage(null);
            plugin.getServer().getOnlinePlayers().stream().filter(staff ->
                    plugin.getGameProfile(staff.getUniqueId()).hasRank(Rank.ADMIN)).forEach(staff ->
                    staff.sendMessage(new ColorBuilder(ChatColor.DARK_GRAY).bold().create() + "[" + new ColorBuilder(ChatColor.RED).bold().create() + "-"
                            + new ColorBuilder(ChatColor.DARK_GRAY).bold().create() + "] " + ChatColor.WHITE + event.getPlayer().getName()));
        } else {
            event.setQuitMessage(new ColorBuilder(ChatColor.DARK_GRAY).bold().create() + "[" + new ColorBuilder(ChatColor.RED).bold().create() + "-"
                    + new ColorBuilder(ChatColor.DARK_GRAY).bold().create() + "] " + ChatColor.WHITE + event.getPlayer().getName());
        }

        gameProfile.setLastOnline(LocalDateTime.now());

        if (DonationUpdater.essenceBar != null)
            if (DonationUpdater.essenceBar.getPlayers().contains(player))
                DonationUpdater.essenceBar.removePlayer(player);

        gameProfile.sync();
    }
}
