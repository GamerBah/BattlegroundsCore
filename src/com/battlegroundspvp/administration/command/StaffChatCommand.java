package com.battlegroundspvp.administration.command;
/* Created by GamerBah on 8/9/2016 */


import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.battlegroundspvp.util.message.MessageBuilder;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StaffChatCommand implements CommandExecutor {
    @Getter
    private static Set<UUID> toggled = new HashSet<>();
    private BattlegroundsCore plugin;

    public StaffChatCommand(BattlegroundsCore plugin) {
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
            if (!gameProfile.hasRank(Rank.HELPER)) {
                plugin.sendNoPermission(player);
            } else {
                if (args.length == 0) {
                    if (!toggled.contains(player.getUniqueId())) {
                        toggled.add(player.getUniqueId());
                        player.sendMessage(ChatColor.GRAY + "Staff Chat toggled " + new MessageBuilder(ChatColor.GREEN).bold().create() + "ON");
                        EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                    } else {
                        toggled.remove(player.getUniqueId());
                        player.sendMessage(ChatColor.GRAY + "Staff Chat toggled " + new MessageBuilder(ChatColor.RED).bold().create() + "OFF");
                        EventSound.playSound(player, EventSound.ACTION_SUCCESS);
                    }
                } else {
                    String message = StringUtils.join(args, ' ', 0, args.length);
                    MessageBuilder.sendStaffMessage(new MessageBuilder(ChatColor.YELLOW).bold().create() + "[STAFF] "
                            + ChatColor.RED + player.getName() + ": " + message);
                }
            }
            return true;
        }
        return false;
    }

}
