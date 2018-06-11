package com.battlegroundspvp.administration.command;
/* Created by GamerBah on 11/8/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.gui.punishment.WarnMenu;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class WarnCommand implements CommandExecutor {

    @Getter
    private static HashMap<UUID, Integer> warned = new HashMap<>();
    private BattlegroundsCore plugin;

    public WarnCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (!gameProfile.hasRank(Rank.HELPER)) {
            plugin.sendNoPermission(player);
            return true;
        }

        if (args.length > 2) {
            plugin.sendIncorrectUsage(player, "/warn <player> [clear]");
            return true;
        }

        if (args.length == 0) {
            WarnMenu warnMenu = new WarnMenu(plugin);
            //warnMenu.openPlayersMenu(player, PunishmentMenus.SortType.NAME_AZ, null, 0);
            return true;
        }

        if (args.length == 1) {
            @SuppressWarnings("deprecation")
            GameProfile targetProfile = plugin.getGameProfile(args[0]);

            if (targetProfile == null) {
                player.sendMessage(ChatColor.RED + "That player has never joined before!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            if (targetProfile.getName().equals(gameProfile.getName())) {
                player.sendMessage(ChatColor.RED + "You aren't able to warn yourself!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            OfflinePlayer target = plugin.getServer().getOfflinePlayer(targetProfile.getUuid());
            WarnMenu warnMenu = new WarnMenu(plugin);
            //warnMenu.openInventory(player, target, null);
        }

        if (args.length == 2) {
            GameProfile targetProfile = plugin.getGameProfile(args[0]);

            if (targetProfile == null) {
                player.sendMessage(ChatColor.RED + "That player has never joined before!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            if (targetProfile.getName().equals(gameProfile.getName())) {
                player.sendMessage(ChatColor.RED + "You can't clear your own warnings!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            if (warned.containsKey(targetProfile.getUuid())) {
                plugin.getServer().getOnlinePlayers().stream().filter(players ->
                        plugin.getGameProfile(players.getUniqueId()).hasRank(Rank.HELPER))
                        .forEach(players -> players.sendMessage(ChatColor.GRAY + player.getName() + ChatColor.RED + " cleared "
                                + ChatColor.GOLD + targetProfile.getName() + "'s (" + warned.get(targetProfile.getUuid()) + ")" + ChatColor.RED + " warnings"));
                warned.remove(targetProfile.getUuid());
                return true;
            } else {
                player.sendMessage(ChatColor.RED + "That player doesn't have any warnings!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }
        }
        return false;
    }

}