package com.battlegroundspvp.utils;
/* Created by GamerBah on 7/12/2017 */

import net.md_5.bungee.api.ChatColor;

public class ColorBuilder {

    private String result = "";
    private ChatColor color;

    public ColorBuilder(ChatColor color) {
        this.color = color;
        this.result = color.toString() + "";
    }

    public ColorBuilder bold() {
        this.result += ChatColor.BOLD.toString() + "";
        return this;
    }

    public ColorBuilder italic() {
        this.result += ChatColor.ITALIC.toString() + "";
        return this;
    }

    public ColorBuilder underline() {
        this.result += ChatColor.UNDERLINE.toString() + "";
        return this;
    }

    public ColorBuilder strike() {
        this.result += ChatColor.STRIKETHROUGH.toString() + "";
        return this;
    }

    public ChatColor baseColor() {
        return color;
    }

    public String create() {
        return result;
    }
}
