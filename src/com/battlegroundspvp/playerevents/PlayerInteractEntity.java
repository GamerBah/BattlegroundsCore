package com.battlegroundspvp.playerevents;
/* Created by GamerBah on 10/27/2017 */

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntity implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        for (BattleModule module : BattleModuleLoader.modules.keySet())
            module.onPlayerInteractEntity(event);
    }

    @EventHandler
    public void onInteract(PlayerArmorStandManipulateEvent event) {
        event.setCancelled(true);
    }

}
