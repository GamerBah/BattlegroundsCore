package com.battlegroundspvp.playerevents;
/* Created by GamerBah on 8/7/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.commands.FreezeCommand;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.runnables.DonationUpdater;
import com.battlegroundspvp.runnables.RegisterRunnable;
import com.battlegroundspvp.runnables.UpdateRunnable;
import com.battlegroundspvp.utils.enums.Rarity;
import com.battlegroundspvp.utils.enums.Time;
import com.battlegroundspvp.utils.messages.ColorBuilder;
import de.Herbystar.TTA.TTA_Methods;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.format.DateTimeFormatter;

public class PlayerJoin implements Listener {

    private BattlegroundsCore plugin;

    public PlayerJoin(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST) {
            event.setKickMessage(new ColorBuilder(ChatColor.RED).bold().create() + "You're not on the whitelist!\n\n"
                    + ChatColor.GRAY + "Access to the Development Server is only given§7to donators with the " + Rank.WARLORD.getColor().create() + Rank.WARLORD.toString() + "§7 rank.\n\n"
                    + ChatColor.GRAY + "You can purchase the rank at our store!\n§e§lstore.battlegroundspvp.com");
            return;
        }

        if (plugin.getGameProfile(event.getUniqueId()) == null) {
            BattlegroundsCore.createNewGameProfile(event.getName(), event.getUniqueId());
            plugin.getGlobalStats().setTotalUniqueJoins(plugin.getGlobalStats().getTotalUniqueJoins() + 1);
        }

        GameProfile gameProfile = plugin.getGameProfile(event.getUniqueId());
        BattlegroundsCore.getGameProfiles().add(gameProfile);

        for (Punishment punishment : gameProfile.getPunishmentData().getBans()) {
            if (!punishment.isPardoned()) {
                GameProfile enforcerProfile = plugin.getGameProfile(punishment.getEnforcerId());
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You were banned by " + ChatColor.GOLD + enforcerProfile.getName()
                        + ChatColor.RED + " for " + ChatColor.GOLD + punishment.getReason().getName() + "\n" + ChatColor.AQUA
                        + punishment.getDate().minusHours(9).format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(PST)'")) + "\n\n" + ChatColor.YELLOW
                        + punishment.getReason().getMessage() + "\n\n" + ChatColor.GRAY + "Appeal your ban on the forums: battlegroundspvp.com/forums");
                return;
            }
        }
        for (Punishment punishment : gameProfile.getPunishmentData().getTempBans()) {
            if (!punishment.isPardoned()) {
                GameProfile enforcerProfile = plugin.getGameProfile(punishment.getEnforcerId());
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You were temporarily banned by " + ChatColor.GOLD + enforcerProfile.getName()
                        + ChatColor.RED + " for " + ChatColor.GOLD + punishment.getReason().getName() + "\n" + ChatColor.AQUA
                        + punishment.getDate().minusHours(9).format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(PST)'")) + "\n\n"
                        + ChatColor.GRAY + "Time Remaining in Ban: " + ChatColor.RED + Time.toString(Time.punishmentTimeRemaining(punishment.getExpiration()), true) + "\n" + ChatColor.YELLOW
                        + punishment.getReason().getMessage() + "\n\n" + ChatColor.GRAY + "You can appeal your ban on the forums: battlegroundspvp.com/forums");
                return;
            }

        }

        if (plugin.getConfig().getBoolean("developmentMode")) {
            if (plugin.getGameProfile(event.getUniqueId()) == null || !plugin.getGameProfile(event.getUniqueId()).hasRank(Rank.HELPER)) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You were not able to join the server because it is in\n" + new ColorBuilder(ChatColor.GOLD).bold().underline().create()
                        + "MAINTENANCE MODE\n\n" + ChatColor.AQUA + "This means that we are fixing bugs, or found another issue we needed to take care of\n\n"
                        + ChatColor.GRAY + "We put the server into Maintenance Mode in order to reduce the risk of\n§7corrupting player data, etc. The server should be open shortly!");
                return;
            }
        }

        if (UpdateRunnable.updating) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, new ColorBuilder(ChatColor.YELLOW).bold().create() + "SERVER IS UPDATING!\n"
                    + ChatColor.RED + "To prevent your data from being corrupted, you didn't connect\n\n" + ChatColor.GRAY + "You should be able to join in a few seconds! Hang in there!");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());
        gameProfile.setOnline(true);

        if (gameProfile.getPassword() == null || gameProfile.getPassword().isEmpty()) {
            event.setJoinMessage(null);
            plugin.respawn(player, (Location) plugin.getConfig().get("locations.afk"));
            player.setWalkSpeed(0F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -50, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false));
            player.setFoodLevel(6);
            player.setSaturation(0);
            player.getInventory().clear();
            player.getInventory().setHeldItemSlot(4);
            BattlegroundsCore.sendRegisterMessage(player);
            plugin.getAwaitingRegistration().add(player);
            BattlegroundsCore.getExecutorService().execute(new RegisterRunnable(plugin, player));
        } else {
            plugin.respawn(player);
            if (gameProfile.getPlayerSettings().isStealthyJoin()) {
                event.setJoinMessage(null);
                plugin.getServer().getOnlinePlayers().stream().filter(staff ->
                        plugin.getGameProfile(staff.getUniqueId()).hasRank(Rank.ADMIN)).forEach(staff ->
                        staff.sendMessage(new ColorBuilder(ChatColor.DARK_GRAY).bold().create() + "[" + new ColorBuilder(ChatColor.GREEN).bold().create() + "+"
                                + new ColorBuilder(ChatColor.DARK_GRAY).bold().create() + "] " + ChatColor.WHITE + event.getPlayer().getName() + ChatColor.GRAY + " (Stealth Join)"));
            } else {
                if (!event.getJoinMessage().startsWith(new ColorBuilder(ChatColor.GOLD).bold().create() + "New! ")) {
                    if (!gameProfile.getName().equals(player.getName())) {
                        String oldName = gameProfile.getPlayer().getName();
                        gameProfile.setName(player.getName());
                        event.setJoinMessage(new ColorBuilder(ChatColor.DARK_GRAY).bold().create() + "[" + new ColorBuilder(ChatColor.GREEN).bold().create() + "+"
                                + new ColorBuilder(ChatColor.DARK_GRAY).bold().create() + "] " + ChatColor.WHITE + event.getPlayer().getName() + ChatColor.GRAY + " (" + oldName + ")");
                    } else {
                        event.setJoinMessage(new ColorBuilder(ChatColor.DARK_GRAY).bold().create() + "[" + new ColorBuilder(ChatColor.GREEN).bold().create() + "+"
                                + new ColorBuilder(ChatColor.DARK_GRAY).bold().create() + "] " + ChatColor.WHITE + event.getPlayer().getName());
                    }
                }
            }
            if (Math.random() <= 0.05) {
                double chance = Math.random();
                Rarity reward = Rarity.COMMON;
                for (Rarity rarity : Rarity.values())
                    if (chance <= Math.pow(rarity.getChance(), 2) && chance >= Math.pow(rarity.getMinChance(Rarity.COMMON), 2))
                        reward = rarity;
                gameProfile.getCratesData().addCrate(reward, 1);
                player.sendMessage(reward.getColor() + "\u00BB " + ChatColor.GRAY + "You found a " + reward.getCrateName() + ChatColor.GRAY + "!");
            }

            if ((FreezeCommand.frozen && !gameProfile.hasRank(Rank.MODERATOR)) || FreezeCommand.frozenPlayers.contains(player) || FreezeCommand.reloadFreeze) {
                player.setWalkSpeed(0F);
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -50, true, false));
                player.setFoodLevel(6);
                player.setSaturation(0);
            } else {
                player.setWalkSpeed(0.2F);
            }

            if (DonationUpdater.essenceBar != null)
                if (!DonationUpdater.essenceBar.getPlayers().contains(player))
                    DonationUpdater.essenceBar.addPlayer(player);
        }

        player.setPlayerListName((gameProfile.hasRank(Rank.WARRIOR) ? gameProfile.getRank().getColor().create() + "" + ChatColor.BOLD + gameProfile.getRank().getName().toUpperCase() + " " : "")
                + (gameProfile.hasRank(Rank.WARRIOR) ? ChatColor.WHITE : ChatColor.GRAY) + player.getName());

        TTA_Methods.sendTablist(player, ChatColor.AQUA + "   You're playing on " + new ColorBuilder(ChatColor.GOLD).bold().create() + "BATTLEGROUNDS   ",
                ChatColor.YELLOW + "Visit our store! store.battlegroundspvp.com");
    }
}
