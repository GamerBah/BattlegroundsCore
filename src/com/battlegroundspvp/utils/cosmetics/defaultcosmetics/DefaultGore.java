package com.battlegroundspvp.utils.cosmetics.defaultcosmetics;
/* Created by GamerBah on 10/29/2017 */

import com.battlegroundspvp.utils.cosmetics.Gore;
import com.battlegroundspvp.utils.enums.Rarity;
import com.battlegroundspvp.utils.inventories.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DefaultGore extends Gore {

    public DefaultGore() {
        super(0, ChatColor.GRAY + "None", new ItemBuilder(Material.BARRIER).name(ChatColor.GRAY + "None"), Rarity.COMMON, null);
    }

    @Override
    public void onKill(Player p1, Player p2) {
    }
}
