package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 8/7/2016 */

import com.battlegroundspvp.utils.messages.ColorBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

@Getter
@AllArgsConstructor
public enum Rank {
    OWNER(9, "Owner", new ColorBuilder(ChatColor.RED).bold(), 100),
    DEVELOPER(8, "Developer", new ColorBuilder(ChatColor.GOLD).bold(), 75),
    ADMIN(7, "Admin", new ColorBuilder(ChatColor.RED).bold(), 50),
    MODERATOR(6, "Mod", new ColorBuilder(ChatColor.DARK_AQUA).bold(), 25),
    HELPER(5, "Helper", new ColorBuilder(ChatColor.GREEN).bold(), 15),
    WARLORD(4, "Warlord", new ColorBuilder(ChatColor.AQUA).bold(), 10),
    CONQUEROR(3, "Conqueror", new ColorBuilder(ChatColor.YELLOW).bold(), 8),
    GLADIATOR(2, "Gladiator", new ColorBuilder(ChatColor.LIGHT_PURPLE).bold(), 6),
    WARRIOR(1, "Warrior", new ColorBuilder(ChatColor.DARK_PURPLE).bold(), 4),
    DEFAULT(0, "Default", new ColorBuilder(ChatColor.GRAY), 0);

    private final int id;
    private final String name;
    private final ColorBuilder color;
    private final int level;

    public static Rank fromString(final String name) {
        Rank r = null;
        for (Rank rank : Rank.values())
            if (name.trim().toUpperCase().equals(rank.toString()))
                r = rank;
        if (r == null)
            throw new IllegalArgumentException("Name doesn't match a Rank");
        return r;
    }

    public static Rank ofId(final int id) {
        for (Rank rank : Rank.values())
            if (rank.id == id)
                return rank;
        return DEFAULT;
    }

}
