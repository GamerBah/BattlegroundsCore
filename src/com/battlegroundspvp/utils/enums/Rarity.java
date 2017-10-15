package com.battlegroundspvp.utils.enums;/* Created by GamerBah on 8/7/2016 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

@AllArgsConstructor
@Getter
public enum Rarity {

    COMMON("Common", ChatColor.GRAY, 100, 46),
    RARE("Rare", ChatColor.BLUE, 45, 25),
    EPIC("Epic", ChatColor.GOLD, 25, 13),
    LEGENDARY("Legendary", ChatColor.LIGHT_PURPLE, 12, 1);

    private String name;
    private ChatColor color;
    private int maxChance;
    private int minChance;

}
