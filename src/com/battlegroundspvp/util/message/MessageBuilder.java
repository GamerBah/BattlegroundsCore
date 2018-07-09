package com.battlegroundspvp.util.message;
/* Created by GamerBah on 7/12/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.GameProfile;
import com.battlegroundspvp.util.enums.EventSound;
import com.battlegroundspvp.util.enums.FontInfo;
import com.battlegroundspvp.util.enums.Rank;
import com.battlegroundspvp.util.manager.GameProfileManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.Arrays;

public class MessageBuilder {

    private final static int CENTER_PX = 154;

    private String result;
    private ChatColor color;

    public MessageBuilder(ChatColor color) {
        this.color = color;
        this.result = color.toString() + "";
    }

    public MessageBuilder bold() {
        this.result += ChatColor.BOLD.toString() + "";
        return this;
    }

    public MessageBuilder italic() {
        this.result += ChatColor.ITALIC.toString() + "";
        return this;
    }

    public MessageBuilder underline() {
        this.result += ChatColor.UNDERLINE.toString() + "";
        return this;
    }

    public MessageBuilder strike() {
        this.result += ChatColor.STRIKETHROUGH.toString() + "";
        return this;
    }

    public ChatColor baseColor() {
        return color;
    }

    public String create() {
        return result;
    }

    public static String centered(final String string) {
        if (string == null || string.isEmpty())
            throw new IllegalArgumentException("string cannot be null or empty");

        int pixelLength = 0;
        boolean previousCode = false;
        boolean bold = false;

        for (char c : string.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                bold = (c == 'l' || c == 'L');
            } else {
                FontInfo fontInfo = FontInfo.getFontInfo(c);
                pixelLength += bold ? fontInfo.getBoldLength() : fontInfo.getLength();
                pixelLength++;
            }
        }

        int halvedMessageSize = pixelLength / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = FontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb.toString() + string;
    }

    public static void sendStaffMessage(final Rank requiredRank, final String... messages) {
        BattlegroundsCore.getInstance().getServer().getOnlinePlayers().stream().filter(player -> {
            GameProfile gameProfile = GameProfileManager.getGameProfile(player.getUniqueId());
            return gameProfile != null && gameProfile.hasRank(requiredRank);
        }).forEach(staff -> {
            Arrays.asList(messages).forEach((staff::sendMessage));
            EventSound.playSound(staff, EventSound.CLICK);
        });
    }

    public static void sendStaffMessage(final Rank requiredRank, final BaseComponent... baseComponents) {
        BattlegroundsCore.getInstance().getServer().getOnlinePlayers().stream().filter(player -> {
            GameProfile gameProfile = GameProfileManager.getGameProfile(player.getUniqueId());
            return gameProfile != null && gameProfile.hasRank(requiredRank);
        }).forEach(staff -> {
            Arrays.asList(baseComponents).forEach(((staff.spigot()::sendMessage)));
            EventSound.playSound(staff, EventSound.CLICK);
        });
    }

    public static void sendStaffMessage(final String... messages) {
        sendStaffMessage(Rank.HELPER, messages);
    }

    public static void sendStaffMessage(final BaseComponent... baseComponents) {
        sendStaffMessage(Rank.HELPER, baseComponents);
    }
}
