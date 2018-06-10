package com.battlegroundspvp.util.message;

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import net.md_5.bungee.api.chat.BaseComponent;

import static org.bukkit.ChatColor.*;

public class FriendMessages {

    private static TextComponentMessages tcm = new TextComponentMessages();

    public static void sendRequested(GameProfile gameProfile, GameProfile targetProfile) {
        gameProfile.sendMessage(WHITE + "   \u00AB " + GREEN + "Your request was sent to " + YELLOW + targetProfile.getName() + WHITE + " \u00BB").playSound(EventSound.REQUEST);

        targetProfile.playSound(EventSound.REQUEST)
                .sendMessage(" ")
                .sendMessage(GOLD + "   \u00AB " + WHITE + "========================================" + GOLD + " \u00BB")
                .sendMessage(" ")
                .sendMessage(YELLOW + "           " + gameProfile.getName() + AQUA + " has sent you a friend request!")
                .sendMessage(" ");
        BaseComponent component = tcm.centerTextSpacesLeft();
        component.addExtra(tcm.friendAcceptButton());
        component.addExtra(tcm.centerTextSpacesMiddle());
        component.addExtra(tcm.friendDeclineButton());
        if (targetProfile.isOnline())
            targetProfile.getPlayer().spigot().sendMessage(component);
        targetProfile.sendMessage(GOLD + "   \u00AB " + WHITE + "========================================" + GOLD + " \u00BB");
        targetProfile.sendMessage(" ");
        if (!targetProfile.isOnline())
            BattlegroundsCore.getOfflineMessages().put(targetProfile.getUuid(),
                    () -> targetProfile.sendMessage(WHITE + "   \u00AB " + AQUA + "You received a friend request from " + YELLOW + gameProfile.getName() + WHITE + " \u00BB"));
    }

    public static void sendAccepted(GameProfile gameProfile, GameProfile targetProfile) {
        gameProfile.sendMessage(GREEN + "   \u00AB " + YELLOW + "You are now friends with " + AQUA + targetProfile.getName() + GREEN + " \u00BB").playSound(EventSound.REQUEST_ACCEPTED);
        if (!gameProfile.isOnline())
            BattlegroundsCore.getOfflineMessages().put(gameProfile.getUuid(),
                    () -> gameProfile.sendMessage(GREEN + "   \u00AB " + AQUA + targetProfile.getName() + YELLOW + " accepted your friend request!" + GREEN + " \u00BB"));

        targetProfile.sendMessage(GREEN + "   \u00AB " + YELLOW + "You are now friends with " + AQUA + gameProfile.getName() + GREEN + " \u00BB").playSound(EventSound.REQUEST_ACCEPTED);
    }

    public static void sendDeclined(GameProfile gameProfile, GameProfile targetProfile) {
        gameProfile.sendMessage(RED + "   \u00AB " + AQUA + targetProfile.getName() + RED + "declined " + YELLOW + "your friend request" + RED + " \u00BB").playSound(EventSound.REQUEST_DENIED);
        targetProfile.sendMessage(RED + "   \u00AB " + YELLOW + "You " + RED + "declined " + AQUA + gameProfile.getName() + YELLOW + "'s friend request!" + RED + " \u00BB");
    }
}

