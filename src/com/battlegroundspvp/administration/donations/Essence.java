package com.battlegroundspvp.administration.donations;
/* Created by GamerBah on 8/17/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.runnables.DonationUpdater;
import com.battlegroundspvp.utils.enums.Time;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;

public class Essence {

    private BattlegroundsCore plugin = BattlegroundsCore.getInstance();

    public static Type fromName(String string) {
        for (Type type : Type.values())
            if (type.getDisplayName(false).equals(string)) return type;
        return Type.ONE_HOUR_50_PERCENT;
    }

    public static Type fromId(int id) {
        for (Type type : Type.values())
            if (type.getId() == id) return type;
        return Type.ONE_HOUR_50_PERCENT;
    }

    public void activateEssence(Player player, Essence.Type type) {
        plugin.getConfig().set("essence.active", true);
        plugin.getConfig().set("essence.id", type.getId());
        plugin.getConfig().set("essence.owner", player.getName());
        plugin.getConfig().set("essence.timeRemaining", type.getDuration() * 60 * 60);
        plugin.saveConfig();
        plugin.getGameProfile(player.getUniqueId()).getEssenceData().removeEssence(type);
        // TODO:
        // plugin.getGlobalStats().setTotalUsedEssences(plugin.getGlobalStats().getTotalUsedEssences() + 1);

        long milliseconds = plugin.getConfig().getInt("essence.timeRemaining") * 1000;
        DonationUpdater.essenceBar = Bukkit.createBossBar(type.getChatColor() + Time.toString(milliseconds, true)
                + ChatColor.GRAY + " remaining in " + type.getChatColor() + plugin.getConfig().getString("essence.owner") + ChatColor.GRAY + "'s Battle Essence "
                + type.getChatColor() + "(+" + type.getPercent() + "%)", type.barColor, BarStyle.SOLID);
        for (Player players : Bukkit.getServer().getOnlinePlayers())
            DonationUpdater.essenceBar.addPlayer(players);
    }

    public void removeActiveEssence() {
        plugin.getConfig().set("essence.active", false);
        plugin.getConfig().set("essence.id", 0);
        plugin.getConfig().set("essence.owner", "");
        plugin.getConfig().set("essence.timeRemaining", 0);
        plugin.getConfig().set("essenceThanks", "");
        plugin.saveConfig();
    }

    @AllArgsConstructor
    @Getter
    public enum Type {
        ONE_HOUR_50_PERCENT(0, 1, 50, ChatColor.GREEN, BarColor.GREEN),
        ONE_HOUR_100_PERCENT(1, 1, 100, ChatColor.GREEN, BarColor.GREEN),
        ONE_HOUR_150_PERCENT(2, 1, 150, ChatColor.GREEN, BarColor.GREEN),
        THREE_HOUR_50_PERCENT(3, 3, 50, ChatColor.AQUA, BarColor.BLUE),
        THREE_HOUR_100_PERCENT(4, 3, 100, ChatColor.AQUA, BarColor.BLUE),
        THREE_HOUR_150_PERCENT(5, 3, 150, ChatColor.AQUA, BarColor.BLUE),
        SIX_HOUR_50_PERCENT(6, 6, 50, ChatColor.LIGHT_PURPLE, BarColor.PINK),
        SIX_HOUR_100_PERCENT(7, 6, 100, ChatColor.LIGHT_PURPLE, BarColor.PINK),
        SIX_HOUR_150_PERCENT(8, 6, 150, ChatColor.LIGHT_PURPLE, BarColor.PINK);

        private int id;
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
