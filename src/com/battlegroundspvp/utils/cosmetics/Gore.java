package com.battlegroundspvp.utils.cosmetics;
/* Created by GamerBah on 10/25/2017 */

import com.battlegroundspvp.utils.enums.Rarity;
import com.battlegroundspvp.utils.inventories.ItemBuilder;
import org.bukkit.entity.Player;

public abstract class Gore extends Cosmetic {

    public Gore(final Integer id, final String name, final ItemBuilder item, final Rarity rarity, final ServerType serverType) {
        super(id, name, item, rarity, EffectType.KILL_EFFECT, serverType);
    }

    public abstract void onKill(Player killer, Player player);

}
