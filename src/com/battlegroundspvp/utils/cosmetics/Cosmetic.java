package com.battlegroundspvp.utils.cosmetics;
/* Created by GamerBah on 9/8/2016 */

import com.battlegroundspvp.utils.cosmetics.defaultcosmetics.DefaultGore;
import com.battlegroundspvp.utils.cosmetics.defaultcosmetics.DefaultParticlePack;
import com.battlegroundspvp.utils.cosmetics.defaultcosmetics.DefaultWarcry;
import com.battlegroundspvp.utils.enums.Rarity;
import com.battlegroundspvp.utils.inventories.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

public abstract class Cosmetic {

    @Getter
    private int id;
    @Getter
    private String name = "";
    @Getter
    private ItemBuilder item = new ItemBuilder(Material.AIR);
    @Getter
    private Rarity rarity = Rarity.COMMON;
    @Getter
    private EffectType effectType;
    @Getter
    private ServerType serverType;

    public Cosmetic(final Integer id, final String name, final ItemBuilder item, final Rarity rarity, final EffectType effectType, final ServerType serverType) {
        this.id = id;
        item.name(rarity.getColor() + (rarity.equals(Rarity.EPIC) || rarity.equals(Rarity.LEGENDARY) ? "" + ChatColor.BOLD : "") + name);
        this.name = name;
        this.item = item;
        this.rarity = rarity;
        this.effectType = effectType;
        this.serverType = serverType;
    }

    public static Cosmetic fromId(final Cosmetic.ServerType serverType, final int id) {
        if (id == 0) return new DefaultParticlePack();
        if (id == 1) return new DefaultWarcry();
        if (id == 2) return new DefaultGore();
        for (Cosmetic cosmetic : CosmeticManager.getAllCosmetics())
            if (cosmetic.getId() == id && cosmetic.getServerType() == serverType)
                return cosmetic;
        return null;
    }

    public String getFullDisplayName() {
        return name + " " + effectType.name;
    }

    @AllArgsConstructor
    public enum EffectType {
        PARTICLE_PACK("Particle Pack"),
        KILL_SOUND("Warcry"),
        KILL_EFFECT("Gore");

        private String name;
    }

    public enum ServerType {
        LOBBY, KITPVP
    }
}
