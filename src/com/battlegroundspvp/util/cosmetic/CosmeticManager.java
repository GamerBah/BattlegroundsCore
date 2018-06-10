package com.battlegroundspvp.util.cosmetic;
/* Created by GamerBah on 10/25/2017 */

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.util.cosmetic.defaultcosmetics.DefaultGore;
import com.battlegroundspvp.util.cosmetic.defaultcosmetics.DefaultParticlePack;
import com.battlegroundspvp.util.cosmetic.defaultcosmetics.DefaultWarcry;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;

public class CosmeticManager implements Listener {

    private final BattlegroundsCore plugin;

    @Getter
    private static ArrayList<Cosmetic> rewardableCosmetics = new ArrayList<>();
    @Getter
    private static ArrayList<Cosmetic> allCosmetics = new ArrayList<>();

    public CosmeticManager(BattlegroundsCore plugin) {
        this.plugin = plugin;
        allCosmetics.add(new DefaultParticlePack());
        allCosmetics.add(new DefaultGore());
        allCosmetics.add(new DefaultWarcry());
        for (BattleModule module : BattleModuleLoader.modules.keySet()) {
            rewardableCosmetics.addAll(module.getCosmetics());
            allCosmetics.addAll(module.getCosmetics());
        }
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        for (BattleModule module : BattleModuleLoader.modules.keySet()) {
            module.getActiveWarcry(killer).onKill(killer, player);
            module.getActiveGore(killer).onKill(killer, player);
        }
    }

}
