package com.battlegroundspvp.commands;

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.utils.enums.ColorBuilder;
import com.battlegroundspvp.utils.enums.EventSound;
import com.battlegroundspvp.utils.enums.Time;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommand implements CommandExecutor {
    private BattlegroundsCore plugin;

    public MessageCommand(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        GameProfile gameProfile = BattlegroundsCore.getInstance().getGameProfile(player.getUniqueId());

        if (gameProfile.isMuted()) {
            MessageCommand.sendErrorMessage(gameProfile);
            return true;
        }

        if (args.length <= 1) {
            plugin.sendIncorrectUsage(player, ChatColor.RED + "/" + label + " <player> <message>");
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);

        if (target == player) {
            player.sendMessage(ChatColor.RED + "What would be the point in messaging yourself?");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        if (!plugin.getGameProfile(target.getUniqueId()).getPlayerSettings().isPrivateMessaging()) {
            player.sendMessage(ChatColor.RED + "That player isn't accepting private messages!");
            EventSound.playSound(player, EventSound.ACTION_FAIL);
            return true;
        }

        plugin.getMessagers().put(player.getUniqueId(), target.getUniqueId());
        plugin.getMessagers().put(target.getUniqueId(), player.getUniqueId());

        String message = StringUtils.join(args, ' ', 1, args.length);

        if (BattlegroundsCore.getAfk().contains(target.getUniqueId())) {
            player.sendMessage(ChatColor.AQUA + target.getName() + " is AFK, so they might not see your message");
            EventSound.playSound(player, EventSound.COMMAND_NEEDS_CONFIRMATION);
        }

        player.sendMessage(ChatColor.DARK_AQUA + "You" + ChatColor.RED + " \u00BB " + new ColorBuilder(ChatColor.AQUA).bold().create() + target.getName() + ChatColor.WHITE + ": " + ChatColor.AQUA + message.trim());
        target.sendMessage(new ColorBuilder(ChatColor.AQUA).bold().create() + player.getName() + ChatColor.RED + " \u00BB " + ChatColor.DARK_AQUA + "You" + ChatColor.WHITE + ": " + ChatColor.AQUA + message);

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2, 2);
        target.playSound(target.getLocation(), Sound.BLOCK_NOTE_HARP, 2, 2);

        return true;
    }

    public static void sendErrorMessage(GameProfile gameProfile) {
        BaseComponent baseComponent = new TextComponent(ChatColor.RED + "You are muted! " + ChatColor.GRAY + "(Hover to view details)");
        baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Muted by: "
                + ChatColor.WHITE + BattlegroundsCore.getInstance() + "\n" + ChatColor.GRAY + "Reason: "
                + ChatColor.WHITE + gameProfile.getCurrentMute().getReason().getName() + "\n" + ChatColor.GRAY + "Time Remaining: " + ChatColor.WHITE +
                Time.toString(Time.punishmentTimeRemaining(gameProfile.getCurrentMute().getExpiration()), true)).create()));
        gameProfile.getPlayer().spigot().sendMessage(baseComponent);
        EventSound.playSound(gameProfile.getPlayer(), EventSound.ACTION_FAIL);
    }
}
