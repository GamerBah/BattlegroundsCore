package com.battlegroundspvp.administration.donations;
/* Created by GamerBah on 10/12/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.ColorBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class Crate {

    private BattlegroundsCore plugin = BattlegroundsCore.getInstance();

    public static Essence.Type typeFromName(String string) {
        for (Essence.Type type : Essence.Type.values()) {
            if (type.getDisplayName(false).equals(string)) {
                return type;
            }
        }
        return null;
    }

    public void open(Player player, Crate.Type type) {
        // TODO: Open Crate
    }

    @AllArgsConstructor
    @Getter
    public enum Type {
        COMMON(100, 100, new ColorBuilder(ChatColor.GRAY).create()),
        RARE(20, 250, new ColorBuilder(ChatColor.BLUE).create()),
        EPIC(12, 375, new ColorBuilder(ChatColor.GOLD).bold().create()),
        LEGENDARY(5, 500, new ColorBuilder(ChatColor.LIGHT_PURPLE).bold().create()),
        LIMITED(0, -1, new ColorBuilder(ChatColor.GREEN).bold().create()),
        GIFT(0, -1, new ColorBuilder(ChatColor.AQUA).bold().create());

        private double chance;
        private int cost;
        private String color;

        public String getName() {
            return color + (chance > 12 ? this.toString().toLowerCase() + " Battle Crate" : this.toString().toUpperCase() + " BATTLE CRATE");
        }
    }

}
