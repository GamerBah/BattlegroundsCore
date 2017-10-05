package com.battlegroundspvp.utils.friends;

import com.battlegroundspvp.Core;
import com.battlegroundspvp.administration.data.GameProfile;
import org.bukkit.entity.Player;

public class FriendUtils {
    private Core plugin;

    public FriendUtils(Core plugin) {
        this.plugin = plugin;
    }

    public void createPendingRequest(Player sender, Player target) {
        FriendMessages friendMessages = new FriendMessages(plugin);
        Core.pendingFriends.put(target, sender);
        friendMessages.sendRequestMessage(sender, target);
    }

    public void removePendingRequest(Player target, boolean accepted) {
        FriendMessages friendMessages = new FriendMessages(plugin);
        if (accepted) {
            friendMessages.sendAcceptMessage(target, Core.pendingFriends.get(target));
        } else {
            friendMessages.sendDeclineMessage(target, Core.pendingFriends.get(target));
        }
        Core.pendingFriends.remove(target);
    }

    public void addFriend(Player sender, Player target) {
        GameProfile gameProfile = plugin.getGameProfile(sender.getUniqueId());
        GameProfile targetData = plugin.getGameProfile(target.getUniqueId());
        if (gameProfile.getFriends() == null) {
            gameProfile.setFriends(targetData.getUuid() + ",");
        } else {
            gameProfile.setFriends(gameProfile.getFriends() + targetData.getUuid() + ",");
        }
        if (targetData.getFriends() == null) {
            targetData.setFriends(gameProfile.getUuid() + ",");
        } else {
            targetData.setFriends(targetData.getFriends() + gameProfile.getUuid() + ",");
        }
        removePendingRequest(target, true);
    }

    public void deleteFriend(Player player, Player target) {
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());
        GameProfile targetData = plugin.getGameProfile(target.getUniqueId());
        if (gameProfile.getFriends().contains(targetData.getUuid() + ",")) {
            gameProfile.setFriends(gameProfile.getFriends().replace(targetData.getName() + ",", ""));
            targetData.setFriends(targetData.getFriends().replace(gameProfile.getName() + ",", ""));
        }
    }

    public boolean hasPendingRequest(Player target, Player sender) {
        if (Core.pendingFriends.containsKey(target)) {
            return Core.pendingFriends.get(target).equals(sender);
        }
        return false;
    }

    public boolean areFriends(Player player, Player target) {
        GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());
        GameProfile targetData = plugin.getGameProfile(target.getUniqueId());
        if (gameProfile.getFriends() == null) {
            return false;
        }
        return gameProfile.getFriends().contains(targetData.getUuid() + ",");
    }

    public Player getRequester(Player target) {
        return Core.pendingFriends.get(target);
    }

}
