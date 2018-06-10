package com.battlegroundspvp.util.message;
/* Created by GamerBah on 7/12/2017 */

import com.battlegroundspvp.util.enums.FontInfo;
import net.md_5.bungee.api.ChatColor;

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
}
