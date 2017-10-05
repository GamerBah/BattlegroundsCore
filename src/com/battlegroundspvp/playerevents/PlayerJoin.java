package com.battlegroundspvp.playerevents;
/* Created by GamerBah on 8/7/2016 */

import com.battlegroundspvp.Core;
import com.battlegroundspvp.administration.commands.FreezeCommand;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.runnables.AutoUpdate;
import com.battlegroundspvp.utils.enums.ColorBuilder;
import com.battlegroundspvp.utils.enums.Time;
import de.Herbystar.TTA.TTA_Methods;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class PlayerJoin implements Listener {

    private Core plugin;

    public PlayerJoin(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (plugin.getGameProfile(event.getUniqueId()) == null) {
            Core.createNewGameProfile(event.getName(), event.getUniqueId());
            //plugin.getGlobalStats().addUniqueJoin();
        }

        ArrayList<Punishment> punishments = plugin.getPlayerPunishments().get(event.getUniqueId());
        if (punishments != null) {
            for (int i = 0; i < punishments.size(); i++) {
                Punishment punishment = punishments.get(i);
                if (punishment.getType().equals(Punishment.Type.BAN)) {
                    if (!punishment.isPardoned()) {
                        GameProfile gameProfile = plugin.getGameProfile(punishment.getEnforcer());
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You were banned by " + ChatColor.GOLD + gameProfile.getPlayer().getName()
                                + ChatColor.RED + " for " + ChatColor.GOLD + punishment.getReason().getName() + "\n" + ChatColor.AQUA
                                + punishment.getDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(CST)'")) + "\n\n" + ChatColor.YELLOW
                                + punishment.getReason().getMessage() + "\n\n" + ChatColor.GRAY + "Appeal your ban on the forums: battlegroundspvp.com/forums");
                        return;
                    }
                }
                if (punishment.getType().equals(Punishment.Type.TEMP_BAN)) {
                    if (!punishment.isPardoned()) {
                        GameProfile gameProfile = plugin.getGameProfile(punishment.getEnforcer());
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You were temporarily banned by " + ChatColor.GOLD + gameProfile.getPlayer().getName()
                                + ChatColor.RED + " for " + ChatColor.GOLD + punishment.getReason().getName() + "\n" + ChatColor.AQUA
                                + punishment.getDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(CST)'")) + "\n\n"
                                + ChatColor.GRAY + "Time Remaining in Ban: " + ChatColor.RED + Time.toString(Time.punishmentTimeRemaining(punishment.getExpiration()), true) + "\n" + ChatColor.YELLOW
                                + punishment.getReason().getMessage() + "\n\n" + ChatColor.GRAY + "You can appeal your ban on the forums: battlegroundspvp.com/forums");
                        return;
                    }
                }
            }
        }

        if (plugin.getConfig().getBoolean("developmentMode")) {
            if (plugin.getGameProfile(event.getUniqueId()) == null || !plugin.getGameProfile(event.getUniqueId()).hasRank(Rank.HELPER)) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You were not able to join the server because it is in\n" + ColorBuilder.GOLD.bold().underline().create()
                        + "MAINTENANCE MODE\n\n" + ChatColor.AQUA + "This means that we are fixing bugs, or found another issue we needed to take care of\n\n"
                        + ChatColor.GRAY + "We put the server into Maintenance Mode in order to reduce the risk of\n§7corrupting player data, etc. The server should be open shortly!");
                return;
            }
        }

        if (AutoUpdate.updating) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ColorBuilder.YELLOW.bold().create() + "SERVER IS UPDATING!\n"
                    + ChatColor.RED + "To prevent your data from being corrupted, you didn't connect\n\n" + ChatColor.GRAY + "You should be able to join in a few seconds! Hang in there!");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (!player.hasPlayedBefore()) {
            event.setJoinMessage(ColorBuilder.GOLD.bold().create() + "New! " + ColorBuilder.DARK_GRAY.bold().create() + "[" + ColorBuilder.GREEN.bold().create() + "+"
                    + ColorBuilder.DARK_GRAY.bold().create() + "] " + ChatColor.WHITE + event.getPlayer().getName());
        } else {
            /*if (gameProfile.getPlayerSettings().isStealthyJoin()) {
                event.setJoinMessage(null);
                plugin.getServer().getOnlinePlayers().stream().filter(staff ->
                        plugin.getGameProfile(staff.getUniqueId()).hasRank(Rank.ADMIN)).forEach(staff ->
                        staff.sendMessage(ColorBuilder.DARK_GRAY.bold().create() + "[" + ColorBuilder.GREEN.bold().create() + "+"
                                + ColorBuilder.DARK_GRAY.bold().create() + "] " + ChatColor.WHITE + event.getPlayer().getName() + ChatColor.GRAY + " (Stealth Join)"));
            } else {*/
                if (!player.getName().equals(gameProfile.getPlayer().getName())) {
                    String oldName = gameProfile.getPlayer().getName();
                    gameProfile.setName(player.getName());
                    event.setJoinMessage(ColorBuilder.DARK_GRAY.bold().create() + "[" + ColorBuilder.GREEN.bold().create() + "+"
                            + ColorBuilder.DARK_GRAY.bold().create() + "] " + ChatColor.WHITE + event.getPlayer().getName() + ChatColor.GRAY + " (" + oldName + ")");
                } else {
                    event.setJoinMessage(ColorBuilder.DARK_GRAY.bold().create() + "[" + ColorBuilder.GREEN.bold().create() + "+"
                            + ColorBuilder.DARK_GRAY.bold().create() + "] " + ChatColor.WHITE + event.getPlayer().getName());
                }
            //}
        }

        if ((FreezeCommand.frozen && !gameProfile.hasRank(Rank.MODERATOR)) || FreezeCommand.frozenPlayers.contains(player) || FreezeCommand.reloadFreeze) {
            player.setWalkSpeed(0F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -50, true, false));
            player.setFoodLevel(6);
            player.setSaturation(0);
        } else {
            player.setWalkSpeed(0.2F);
        }

        player.setPlayerListName((gameProfile.hasRank(Rank.WARRIOR) ? gameProfile.getRank().getColor() + "" + ChatColor.BOLD + gameProfile.getRank().getName().toUpperCase() + " " : "")
                + (gameProfile.hasRank(Rank.WARRIOR) ? ChatColor.WHITE : ChatColor.GRAY) + player.getName());

        TTA_Methods.sendTablist(player, ChatColor.AQUA + "You're playing on " + ColorBuilder.GOLD.bold().create() + "BATTLEGROUNDS",
                ChatColor.YELLOW + "Visit our store! store.battlegroundspvp.com");

        plugin.respawn(player);
    }
}
