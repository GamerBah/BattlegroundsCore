package com.battlegroundspvp.utils.cosmetics.defaultcosmetics;
/* Created by GamerBah on 10/29/2017 */

import com.battlegroundspvp.utils.cosmetics.ParticlePack;
import com.battlegroundspvp.utils.enums.Rarity;
import com.battlegroundspvp.utils.inventories.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DefaultParticlePack extends ParticlePack {

    public DefaultParticlePack() {
        super(1, ChatColor.GRAY + "None", new ItemBuilder(Material.BARRIER).name(ChatColor.GRAY + "None"), Rarity.COMMON, null);
    }

    @Override
    public void onMove(Player player) {
    }

    @Override
    public void run() {
    }
}
