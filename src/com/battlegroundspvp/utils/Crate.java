package com.battlegroundspvp.utils;
/* Created by GamerBah on 11/2/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;

public class Crate {

    @Getter
    private final int id;
    @Getter
    private final Location location;
    @Getter
    private final Hologram hologram;

    public Crate(final int id, final Location location) {
        this.id = id;
        this.location = location;
        this.hologram = new Hologram(location.clone().add(0.5, 0.25, 0.5), false,
                new ColorBuilder(ChatColor.YELLOW).bold().create() + "RIGHT CLICK",
                new ColorBuilder(ChatColor.LIGHT_PURPLE).bold().create() + "BATTLE CRATES");
    }

    public Crate(final Location location) {
        this((BattlegroundsCore.getCrates().size() != 0 ? BattlegroundsCore.getCrates().get(BattlegroundsCore.getCrates().size() - 1).id + 1 : 0), location);
    }

    public static Crate fromLocation(Location location) {
        for (Crate crate : BattlegroundsCore.getCrates())
            if (crate.getLocation().hashCode() == location.hashCode()) return crate;
        return null;
    }

    public static Crate fromId(int id) {
        for (Crate crate : BattlegroundsCore.getCrates())
            if (crate.getId() == id) return crate;
        return null;
    }

}
