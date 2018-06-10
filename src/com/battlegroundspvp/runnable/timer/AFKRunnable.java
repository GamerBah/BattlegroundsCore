package com.battlegroundspvp.runnable.timer;
/* Created by GamerBah on 8/24/2016 */


import com.battlegroundspvp.BattlegroundsCore;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AFKRunnable implements Runnable {

    @Getter
    private static HashMap<Player, Integer> afkTimer = new HashMap<>();
    private BattlegroundsCore plugin;

    public AFKRunnable(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers())
            if (afkTimer.containsKey(player) && !plugin.getAwaitingRegistration().contains(player)) {
                if (afkTimer.get(player) < 300)
                    afkTimer.put(player, afkTimer.get(player) + 1);
                if (afkTimer.get(player) == 300)
                    if (!BattlegroundsCore.getAfk().contains(player.getUniqueId()))
                        player.performCommand("afk");
            }
    }
}