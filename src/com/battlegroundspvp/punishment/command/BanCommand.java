package com.battlegroundspvp.punishment.command;
/* Created by GamerBah on 8/7/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.gui.punishment.PunishmentMenus;
import com.battlegroundspvp.punishment.Punishment;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.enums.Time;
import com.gamerbah.inventorytoolkit.InventoryBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;

public class BanCommand implements CommandExecutor {
    private BattlegroundsCore plugin;

    public BanCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());

        if (!gameProfile.hasRank(Rank.ADMIN)) {
            plugin.sendNoPermission(player);
            return true;
        }

        if (args.length != 1) {
            plugin.sendIncorrectUsage(player, ChatColor.RED + "/ban <player>");
            return true;
        }

        GameProfile targetProfile = plugin.getGameProfile(args[0]);

        if (targetProfile == null) {
            player.sendMessage(ChatColor.RED + "That player hasn't joined before!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (targetProfile == gameProfile) {
            player.sendMessage(ChatColor.RED + "You can't ban yourself!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (targetProfile.hasRank(Rank.MODERATOR)) {
            player.sendMessage(ChatColor.RED + "You can't permanently ban that player!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (targetProfile.isBanned()) {
            BanCommand.sendErrorMessage(gameProfile, targetProfile);
            return true;
        }

        new InventoryBuilder(player, new PunishmentMenus().new BanMenu(player, targetProfile)).open();
        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);

        return true;
    }

    public static void sendErrorMessage(GameProfile gameProfile, GameProfile targetProfile) {
        Punishment ban = targetProfile.getCurrentBan();
        GameProfile enforcer = BattlegroundsCore.getInstance().getGameProfile(ban.getEnforcerId());
        BaseComponent baseComponent = new TextComponent(ChatColor.RED + "That player is already banned!");
        baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Banned by: "
                + enforcer.getRank().getColor().create() + "" + ChatColor.BOLD + enforcer.getRank().getName().toUpperCase()
                + ChatColor.WHITE + " " + enforcer.getName() + "\n" + ChatColor.GRAY + "Reason: "
                + ChatColor.GOLD + ban.getReason().getName() + "\n"
                + (ban.getType() == Punishment.Type.TEMP_BAN ? ChatColor.GRAY + "Time Remaining: "
                + ChatColor.YELLOW + Time.toString(Time.punishmentTimeRemaining(ban.getExpiration()), true)
                : ChatColor.GRAY + "Date: " + ChatColor.AQUA + ban.getDate().minusHours(9).format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a '(PST)'")))).create()));
        gameProfile.getPlayer().spigot().sendMessage(baseComponent);
        gameProfile.playSound(EventSound.ACTION_FAIL);
    }
}
