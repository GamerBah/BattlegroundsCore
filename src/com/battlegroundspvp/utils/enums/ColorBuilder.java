package com.battlegroundspvp.utils.enums;
/* Created by GamerBah on 7/12/2017 */

import net.md_5.bungee.api.ChatColor;

public enum ColorBuilder {

    AQUA(ChatColor.AQUA),
    BLACK(ChatColor.BLACK),
    BLUE(ChatColor.BLUE),
    DARK_AQUA(ChatColor.DARK_AQUA),
    DARK_BLUE(ChatColor.DARK_BLUE),
    DARK_GRAY(ChatColor.DARK_GRAY),
    DARK_GREEN(ChatColor.DARK_GREEN),
    DARK_RED(ChatColor.DARK_RED),
    GOLD(ChatColor.GOLD),
    GRAY(ChatColor.GRAY),
    GREEN(ChatColor.GREEN),
    PINK(ChatColor.LIGHT_PURPLE),
    PURPLE(ChatColor.DARK_PURPLE),
    RED(ChatColor.RED),
    WHITE(ChatColor.WHITE),
    YELLOW(ChatColor.YELLOW);

    private String result = "";

    ColorBuilder(ChatColor color) {
        this.result = color.toString() + "";
    }

    public ColorBuilder bold() {
        this.result += ChatColor.BOLD + "";
        return this;
    }

    public ColorBuilder italic() {
        this.result += ChatColor.ITALIC + "";
        return this;
    }

    public ColorBuilder underline() {
        this.result += ChatColor.UNDERLINE + "";
        return this;
    }

    public ColorBuilder strike() {
        this.result += ChatColor.STRIKETHROUGH + "";
        return this;
    }

    public String create() {
        return result;
    }
}
