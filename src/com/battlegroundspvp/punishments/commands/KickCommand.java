package com.battlegroundspvp.punishments.commands;
/* Created by GamerBah on 8/7/2016 */

import com.battlegroundspvp.Core;
import com.battlegroundspvp.administration.commands.WarnCommand;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.menus.Punishment.PunishmentMenus;
import com.battlegroundspvp.punishments.Punishment;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.inventories.InventoryBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class KickCommand implements CommandExecutor {
    private Core plugin;

    public KickCommand(Core plugin) {
        this.plugin = plugin;
    }

    public static void kickPlayer(UUID targetUUID, Player player, HashMap<Punishment.Reason, Integer> map) {
        Core plugin = Core.getInstance();
        GameProfile targetData = plugin.getGameProfile(targetUUID);

        Punishment.Reason reason = null;
        for (Punishment.Reason r : map.keySet()) {
            reason = r;
        }

        Player target = plugin.getServer().getPlayer(targetUUID);
        if (target == null) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "That player isn't online!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return;
        }

        if (reason != null) {
            //plugin.createPunishment(targetData, Punishment.Type.KICK, LocalDateTime.now(), -1, player.getUniqueId(), reason);
            if (!WarnCommand.getWarned().containsKey(targetUUID)) {
                WarnCommand.getWarned().remove(targetUUID);
            }

            final String finalName = reason.getName();
            BaseComponent baseComponent = new TextComponent(ChatColor.RED + player.getName() + " kicked " + ChatColor.RED + target.getName());
            baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Reason: "
                    + ChatColor.GOLD + finalName).create()));
            plugin.getServer().getOnlinePlayers().stream().filter(staff -> plugin.getGameProfile(staff.getUniqueId()).hasRank(Rank.HELPER)).forEach(staff -> staff.spigot().sendMessage(baseComponent));

            target.kickPlayer(ChatColor.RED + "You were kicked by " + ChatColor.GOLD + player.getName() + ChatColor.RED + " for " + ChatColor.GOLD + finalName + "\n"
                    + ChatColor.YELLOW + reason.getMessage() + "\n\n" + ChatColor.GRAY + "If you feel that staff abuse was an issue, please email support@battlegroundspvp.com");

            player.closeInventory();
            Core.punishmentCreation.remove(player);
            //plugin.getGlobalStats().addKick();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (!gameProfile.hasRank(Rank.MODERATOR)) {
            plugin.sendNoPermission(player);
            return true;
        }

        if (args.length != 1) {
            plugin.sendIncorrectUsage(player, "/kick <player>");
            return true;
        }

        @SuppressWarnings("deprecation")
        GameProfile targetData = plugin.getGameProfile(plugin.getServer().getOfflinePlayer(args[0]).getUniqueId());
        if (targetData == null) {
            player.sendMessage(ChatColor.RED + "That player hasn't joined before!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }
        Player target = plugin.getServer().getPlayer(targetData.getUuid());
        if (target == null) {
            player.sendMessage(ChatColor.RED + "That player isn't online!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (targetData == gameProfile) {
            player.sendMessage(ChatColor.RED + "You can't kick yourself!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        new InventoryBuilder(player, new PunishmentMenus().new KickMenu(player, targetData)).open();
        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);

        return true;
    }
}
