package com.battlegroundspvp.util.enums;
/* Created by GamerBah on 8/7/2016 */

import com.battlegroundspvp.util.message.MessageBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Rank {
    OWNER(9, "Owner", new MessageBuilder(ChatColor.RED).bold(), 100),
    DEVELOPER(8, "Dev", new MessageBuilder(ChatColor.GOLD).bold(), 75),
    ADMIN(7, "Admin", new MessageBuilder(ChatColor.RED).bold(), 50),
    MODERATOR(6, "Mod", new MessageBuilder(ChatColor.DARK_AQUA).bold(), 25),
    HELPER(5, "Helper", new MessageBuilder(ChatColor.GREEN).bold(), 15),
    WARLORD(4, "Warlord", new MessageBuilder(ChatColor.AQUA).bold(), 10),
    CONQUEROR(3, "Conqueror", new MessageBuilder(ChatColor.YELLOW).bold(), 8),
    GLADIATOR(2, "Gladiator", new MessageBuilder(ChatColor.LIGHT_PURPLE).bold(), 6),
    WARRIOR(1, "Warrior", new MessageBuilder(ChatColor.DARK_PURPLE).bold(), 4),
    DEFAULT(0, "Default", new MessageBuilder(ChatColor.GRAY), 0);

    private final int id;
    private final String name;
    private final MessageBuilder color;
    private final int level;

    public static Rank fromString(final String name) {
        return Arrays.stream(Rank.values())
                .filter(rank -> name.trim().equalsIgnoreCase(rank.toString()))
                .findFirst()
                .orElse(DEFAULT);
    }

    public static Rank fromId(final int id) {
        return Arrays.stream(Rank.values())
                .filter(rank -> id == rank.getId())
                .findFirst()
                .orElse(DEFAULT);
    }

}
