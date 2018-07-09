package com.battlegroundspvp.command;

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Time;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.battlegroundspvp.util.message.MessageBuilder;
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
        GameProfile gameProfile = GameProfileManager.getGameProfile(player.getUniqueId());

        if (gameProfile != null) {
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

            GameProfile targetProfile = GameProfileManager.getGameProfile(target.getUniqueId());
            if (targetProfile != null && !targetProfile.getPlayerSettings().isPrivateMessaging()) {
                player.sendMessage(ChatColor.RED + "That player isn't accepting private messages!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            BattlegroundsCore.getMessagers().put(player.getUniqueId(), target.getUniqueId());
            BattlegroundsCore.getMessagers().put(target.getUniqueId(), player.getUniqueId());

            String message = StringUtils.join(args, ' ', 1, args.length);

            if (BattlegroundsCore.getAfk().contains(target.getUniqueId())) {
                player.sendMessage(ChatColor.AQUA + target.getName() + " is AFK, so they might not see your message");
                EventSound.playSound(player, EventSound.COMMAND_NEEDS_CONFIRMATION);
            }

            player.sendMessage(ChatColor.DARK_AQUA + "You" + ChatColor.RED + " \u00BB " + new MessageBuilder(ChatColor.AQUA).bold().create() + target.getName() + ChatColor.WHITE + ": " + ChatColor.AQUA + message.trim());
            target.sendMessage(new MessageBuilder(ChatColor.AQUA).bold().create() + player.getName() + ChatColor.RED + " \u00BB " + ChatColor.DARK_AQUA + "You" + ChatColor.WHITE + ": " + ChatColor.AQUA + message);

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2, 2);
            target.playSound(target.getLocation(), Sound.BLOCK_NOTE_HARP, 2, 2);

            return true;
        }
        return false;
    }

    public static void sendErrorMessage(GameProfile gameProfile) {
        GameProfile enforcerProfile = GameProfileManager.getGameProfile(gameProfile.getCurrentMute().getEnforcerId());
        if (enforcerProfile != null) {
            BaseComponent baseComponent = new TextComponent(ChatColor.RED + "You are muted! " + ChatColor.GRAY + "(Hover to view details)");
            baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "Muted by: "
                    + enforcerProfile.getRank().getColor().create() + enforcerProfile.getName() + "\n" + ChatColor.GRAY + "Reason: "
                    + ChatColor.YELLOW + gameProfile.getCurrentMute().getReason().getName() + "\n" + ChatColor.GRAY + "Time Remaining: " + ChatColor.GOLD +
                    Time.toString(Time.punishmentTimeRemaining(gameProfile.getCurrentMute().getExpiration()), true)).create()));
            gameProfile.getPlayer().spigot().sendMessage(baseComponent);
            EventSound.playSound(gameProfile.getPlayer(), EventSound.ACTION_FAIL);
        }
    }
}
