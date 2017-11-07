package com.battlegroundspvp.utils.cosmetics;
/* Created by GamerBah on 10/25/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.enums.Rarity;
import com.battlegroundspvp.utils.inventories.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class ParticlePack extends Cosmetic implements Runnable {

    public ArrayList<Player> idle = new ArrayList<>();

    public ParticlePack(final Integer id, final String name, final ItemBuilder item, final Rarity rarity, final ServerType serverType) {
        super(id, name, item, rarity, EffectType.PARTICLE_PACK, serverType);
    }

    public ParticlePack(final Integer id, final String name, final ItemBuilder item, final Rarity rarity, final ServerType serverType, final Long runTime) {
        super(id, name, item, rarity, EffectType.PARTICLE_PACK, serverType);
        Bukkit.getServer().getScheduler().runTaskTimer(BattlegroundsCore.getInstance(), this, 0, runTime);
    }

    public abstract void onMove(Player player);
}
