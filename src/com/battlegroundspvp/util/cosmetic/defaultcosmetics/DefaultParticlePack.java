package com.battlegroundspvp.util.cosmetic.defaultcosmetics;
/* Created by GamerBah on 10/29/2017 */

import com.battlegroundspvp.util.cosmetic.ParticlePack;
import com.battlegroundspvp.util.enums.Rarity;
import com.gamerbah.inventorytoolkit.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DefaultParticlePack extends ParticlePack {

    public DefaultParticlePack() {
        super(0, ChatColor.GRAY + "None", new ItemBuilder(Material.BARRIER).name(ChatColor.GRAY + "None"), Rarity.COMMON, null);
    }

    @Override
    public void onMove(Player player) {
    }

    @Override
    public void run() {
    }
}
