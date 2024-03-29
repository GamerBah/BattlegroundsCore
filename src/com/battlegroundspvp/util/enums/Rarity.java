package com.battlegroundspvp.util.enums;/* Created by GamerBah on 8/7/2016 */

import com.battlegroundspvp.util.message.MessageBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

@AllArgsConstructor
@Getter
public enum Rarity {

    COMMON("Common", new MessageBuilder(ChatColor.GRAY), 1, 1, 1),
    RARE("Rare", new MessageBuilder(ChatColor.BLUE), 0.45, 1.5, 1),
    EPIC("Epic", new MessageBuilder(ChatColor.GOLD).bold(), 0.24, 2.0, 1),
    LEGENDARY("Legendary", new MessageBuilder(ChatColor.LIGHT_PURPLE).bold(), 0.12, 2.5, 1),
    GIFT("Gifted", new MessageBuilder(ChatColor.AQUA).bold(), 0, 0, -1),
    SEASONAL("Seasonal", new MessageBuilder(ChatColor.GREEN).bold(), 0, 0, -1);

    private String name;
    private MessageBuilder color;
    private double chance;
    private double buff;
    private int crateCost;

    public double getBuffedChance(Rarity buffType) {
        return (this.chance * buffType.buff) + (buffType == COMMON ? 0 : (this.chance / (buffType == LEGENDARY ? 1.5 : buffType == EPIC ? 2.0 : 2.5)));
    }

    public double getMinChance(Rarity buffType) {
        if (this == COMMON) return RARE.getBuffedChance(buffType);
        if (this == RARE) return EPIC.getBuffedChance(buffType);
        if (this == EPIC) return LEGENDARY.getBuffedChance(buffType);
        if (this == LEGENDARY) return 0.0;
        return 1;
    }

    public String getColor() {
        return this.color.create();
    }

    public String getName() {
        return this.color.create() + (this == EPIC || this == LEGENDARY ? this.name.toUpperCase() : this.name);
    }

    public String getCrateName() {
        return getName() + (this == EPIC || this == LEGENDARY ? " BATTLE CRATE" : " Battle Crate");
    }

    public boolean hasRarity(Rarity rarity) {
        return this.chance <= rarity.getChance();
    }

}
