package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 8/7/2016 */

import com.battlegroundspvp.utils.ColorBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

@Getter
@AllArgsConstructor
public enum Rank {
    OWNER("Owner", new ColorBuilder(ChatColor.RED).bold(), 100),
    DEVELOPER("Dev", new ColorBuilder(ChatColor.GOLD).bold(), 75),
    ADMIN("Admin", new ColorBuilder(ChatColor.RED).bold(), 50),
    MODERATOR("Mod", new ColorBuilder(ChatColor.DARK_AQUA).bold(), 25),
    HELPER("Helper", new ColorBuilder(ChatColor.GREEN).bold(), 15),
    WARLORD("Warlord", new ColorBuilder(ChatColor.AQUA).bold(), 10),
    CONQUEROR("Conqueror", new ColorBuilder(ChatColor.YELLOW).bold(), 8),
    GLADIATOR("Gladiator", new ColorBuilder(ChatColor.LIGHT_PURPLE).bold(), 6),
    WARRIOR("Warrior", new ColorBuilder(ChatColor.DARK_PURPLE).bold(), 4),
    DEFAULT("Default", new ColorBuilder(ChatColor.GRAY), 0);

    private String name;
    private ColorBuilder color;
    private int level;

    public static Rank fromString(String name) {
        Rank r = null;
        for (Rank rank : Rank.values())
            if (name.trim().toUpperCase().equals(rank.toString()))
                r = rank;
        if (r == null)
            throw new IllegalArgumentException("Name doesn't match a Rank");
        return r;
    }

}
