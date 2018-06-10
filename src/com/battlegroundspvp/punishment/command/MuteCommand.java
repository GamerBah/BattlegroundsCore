package com.battlegroundspvp.punishment.command;
/* Created by GamerBah on 8/7/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.administration.data.Rank;
import com.battlegroundspvp.gui.punishment.PunishmentMenus;
import com.battlegroundspvp.punishment.Punishment;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Time;
import com.battlegroundspvp.util.gui.InventoryBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public MuteCommand(BattlegroundsCore plugin) {
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

        if (args.length != 1) {
            plugin.sendIncorrectUsage(player, ChatColor.RED + "/mute <player>");
            return true;
        }

        GameProfile targetProfile = plugin.getGameProfile(args[0]);

        if (targetProfile == null) {
            player.sendMessage(ChatColor.RED + "That player hasn't joined before!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (targetProfile == gameProfile) {
            player.sendMessage(ChatColor.RED + "You can't mute yourself!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (targetProfile.hasRank(Rank.MODERATOR)) {
            player.sendMessage(ChatColor.RED + "You can't mute that player!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (targetProfile.isMuted()) {
            sendErrorMessage(gameProfile, targetProfile);
            return true;
        }

        new InventoryBuilder(player, new PunishmentMenus().new MuteMenu(player, targetProfile)).open();
        EventSound.playSound(player, EventSound.INVENTORY_OPEN_SUBMENU);

        return true;
    }

    public static void sendErrorMessage(GameProfile gameProfile, GameProfile targetProfile) {
        Punishment mute = targetProfile.getCurrentMute();
        GameProfile enforcer = BattlegroundsCore.getInstance().getGameProfile(mute.getEnforcerId());
        BaseComponent baseComponent = new TextComponent(ChatColor.RED + "That player is already muted!");
        baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Muted by: "
                + enforcer.getRank().getColor().create() + "" + ChatColor.BOLD + enforcer.getRank().getName().toUpperCase()
                + ChatColor.WHITE + " " + enforcer.getName() + "\n" + ChatColor.GRAY + "Reason: "
                + ChatColor.GOLD + mute.getReason().getName() + "\n" + ChatColor.GRAY + "Time Remaining: " + ChatColor.YELLOW +
                Time.toString(Time.punishmentTimeRemaining(mute.getExpiration()), true)).create()));
        gameProfile.getPlayer().spigot().sendMessage(baseComponent);
        gameProfile.playSound(EventSound.ACTION_FAIL);
    }
}
