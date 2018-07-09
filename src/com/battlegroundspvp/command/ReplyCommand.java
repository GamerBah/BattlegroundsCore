package com.battlegroundspvp.command;

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.battlegroundspvp.util.message.MessageBuilder;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {
    private BattlegroundsCore plugin;

    public ReplyCommand(BattlegroundsCore plugin) {
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

            if (!BattlegroundsCore.getMessagers().containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You have not messaged anyone!");
                return true;
            }

            Player target = plugin.getServer().getPlayer(BattlegroundsCore.getMessagers().get(player.getUniqueId()));

            if (target == null) {
                player.sendMessage(ChatColor.RED + "The player that you previously messaged is no longer online.");
                return true;
            }

            if (!GameProfileManager.getGameProfile(target.getUniqueId()).getPlayerSettings().isPrivateMessaging()) {
                player.sendMessage(ChatColor.RED + "That player isn't accepting private messages anymore!");
                EventSound.playSound(player, EventSound.ACTION_FAIL);
                return true;
            }

            BattlegroundsCore.getMessagers().put(player.getUniqueId(), target.getUniqueId());
            BattlegroundsCore.getMessagers().put(target.getUniqueId(), player.getUniqueId());

            String message = StringUtils.join(args, ' ', 0, args.length);

            if (BattlegroundsCore.getAfk().contains(target.getUniqueId())) {
                player.sendMessage(ChatColor.AQUA + target.getName() + " is AFK, so they might not see your message");
            }

            player.sendMessage(ChatColor.DARK_AQUA + "You" + ChatColor.RED + " \u00BB " + new MessageBuilder(ChatColor.AQUA).bold().create() + target.getName() + ChatColor.WHITE + ": " + ChatColor.AQUA + message.trim());
            target.sendMessage(new MessageBuilder(ChatColor.AQUA).bold().create() + player.getName() + ChatColor.RED + " \u00BB " + ChatColor.DARK_AQUA + "You" + ChatColor.WHITE + ": " + ChatColor.AQUA + message);

            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 2, 2);
            target.playSound(target.getLocation(), Sound.BLOCK_NOTE_HARP, 2, 2);

            return true;
        }
        return false;
    }
}
