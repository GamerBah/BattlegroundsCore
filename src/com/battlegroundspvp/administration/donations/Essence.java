package com.battlegroundspvp.administration.donations;
/* Created by GamerBah on 8/17/2016 */

import com.battlegroundspvp.Core;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

public class Essence {

    private Core plugin = Core.getInstance();

    public static Type typeFromName(String string) {
        for (Type type : Type.values()) {
            if (type.getDisplayName(false).equals(string)) {
                return type;
            }
        }
        return null;
    }

    public void activateEssence(Player player, Essence.Type type) {
        plugin.getConfig().set("essenceActive", true);
        plugin.getConfig().set("essenceOwner", player.getName());
        plugin.getConfig().set("essenceIncrease", type.getPercent());
        plugin.getConfig().set("essenceTime", type.getDuration());
        plugin.getConfig().set("essenceTimeRemaining", type.getDuration() * 60 * 60);
        plugin.saveConfig();
        plugin.getGameProfile(player.getUniqueId()).getEssenceData().removeEssence(type);
        //plugin.getGlobalStats().addUsedEssence();
    }

    public void removeActiveEssence() {
        plugin.getConfig().set("essenceActive", false);
        plugin.getConfig().set("essenceOwner", "");
        plugin.getConfig().set("essenceIncrease", "");
        plugin.getConfig().set("essenceTime", "");
        plugin.getConfig().set("essenceTimeRemaining", "");
        plugin.getConfig().set("essenceThanks", "");
        plugin.saveConfig();
    }

    @AllArgsConstructor
    @Getter
    public enum Type {
        ONE_HOUR_50_PERCENT(1, 50, ChatColor.GREEN, BarColor.GREEN),
        ONE_HOUR_100_PERCENT(1, 100, ChatColor.GREEN, BarColor.GREEN),
        ONE_HOUR_150_PERCENT(1, 150, ChatColor.GREEN, BarColor.GREEN),
        THREE_HOUR_50_PERCENT(3, 50, ChatColor.AQUA, BarColor.BLUE),
        THREE_HOUR_100_PERCENT(3, 100, ChatColor.AQUA, BarColor.BLUE),
        THREE_HOUR_150_PERCENT(3, 150, ChatColor.AQUA, BarColor.BLUE),
        SIX_HOUR_50_PERCENT(6, 50, ChatColor.LIGHT_PURPLE, BarColor.PINK),
        SIX_HOUR_100_PERCENT(6, 100, ChatColor.LIGHT_PURPLE, BarColor.PINK),
        SIX_HOUR_150_PERCENT(6, 150, ChatColor.LIGHT_PURPLE, BarColor.PINK);

        private int duration;
        private int percent;
        private ChatColor chatColor;
        private BarColor barColor;

        public String getDisplayName(final boolean color) {
            if (color)
                return chatColor + "" + duration + " Hour (+" + percent + "%)";
            return duration + " Hour (+" + percent + "%)";
        }
    }
}
