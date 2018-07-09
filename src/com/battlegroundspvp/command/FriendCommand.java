package com.battlegroundspvp.command;
/* Created by GamerBah on 9/7/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.Time;
import com.battlegroundspvp.util.manager.GameProfileManager;
import com.battlegroundspvp.util.message.MessageBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FriendCommand implements CommandExecutor {

    private BattlegroundsCore plugin;

    public FriendCommand(BattlegroundsCore plugin) {
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
            if (args.length == 0 || args.length > 2) {
                plugin.sendIncorrectUsage(player, "/friend <add/accept/remove/decline/list/seen> <username>");
                return true;
            }

            if (args.length == 1) {
                if (!args[0].equalsIgnoreCase("list")) {
                    plugin.sendIncorrectUsage(player, "/friend <add/accept/remove/decline/list/seen> <username>");
                    return true;
                }

                if (args[0].equalsIgnoreCase("list")) {
                    // TODO: Show list in GUI
                    return true;
                }
            }

            if (args.length == 2) {
                if (GameProfileManager.getGameProfile(args[1]) == null) {
                    player.sendMessage(ChatColor.RED + "That player hasn't joined before!");
                    EventSound.playSound(player, EventSound.ACTION_FAIL);
                    return true;
                }
                GameProfile targetProfile = GameProfileManager.getGameProfile(args[1]);
                if (targetProfile != null) {
                    if (args[0].equalsIgnoreCase("add")) {
                        if (gameProfile.hasFriendRequestCooldown(targetProfile)) {
                            player.sendMessage(new MessageBuilder(ChatColor.RED).bold().create() + "Sorry! " + ChatColor.GRAY + "You have to wait " + ChatColor.RED
                                    + Time.toString(gameProfile.getFriendRequestCooldown(targetProfile), true) + ChatColor.GRAY + "before you can send "
                                    + targetProfile.getName() + " another friend request!");
                        }
                        gameProfile.sendFriendRequest(targetProfile);
                        return true;
                    } else if (args[0].equalsIgnoreCase("remove")) {
                        if (!gameProfile.hasFriend(targetProfile)) {
                            player.sendMessage(ChatColor.RED + "You aren't friends with " + targetProfile.getName() + " yet!");
                            EventSound.playSound(player, EventSound.ACTION_FAIL);
                            return true;
                        }
                        gameProfile.removeFriend(targetProfile);
                        player.sendMessage(ChatColor.GRAY + "Removed " + ChatColor.RED + targetProfile.getName() + ChatColor.GRAY + " from your friends list");
                        EventSound.playSound(player, EventSound.CLICK);
                    } else if (args[0].equalsIgnoreCase("accept")) {
                        if (!gameProfile.hasFriendRequestFrom(targetProfile)) {
                            player.sendMessage(ChatColor.RED + "You don't have a friend request from " + targetProfile.getName() + "!");
                            EventSound.playSound(player, EventSound.ACTION_FAIL);
                            return true;
                        }
                        targetProfile.addFriend(gameProfile);
                    } else if (args[0].equalsIgnoreCase("decline")) {
                        if (!gameProfile.hasFriendRequestFrom(targetProfile)) {
                            player.sendMessage(ChatColor.RED + "You don't have a friend request from " + targetProfile.getName() + "!");
                            EventSound.playSound(player, EventSound.ACTION_FAIL);
                            return true;
                        }
                        gameProfile.removeFriend(targetProfile);
                    } else if (args[0].equalsIgnoreCase("seen")) {
                        if (!gameProfile.hasFriend(targetProfile)) {
                            player.sendMessage(ChatColor.RED + "You aren't friends with " + targetProfile.getName() + " yet!");
                            EventSound.playSound(player, EventSound.ACTION_FAIL);
                            return true;
                        }
                        player.sendMessage(ChatColor.GRAY + targetProfile.getName() + " was last online " + ChatColor.GOLD + Time.toString(targetProfile.getLastOnlineTime(), false));
                    } else {
                        plugin.sendIncorrectUsage(player, "/friend <add/accept/remove/decline/list/seen> <username>");
                        return true;
                    }
                }
            }

            return false;
        }
        return false;
    }

}