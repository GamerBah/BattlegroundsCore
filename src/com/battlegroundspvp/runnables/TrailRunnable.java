package com.battlegroundspvp.runnables;
/* Created by GamerBah on 8/20/2016 */

import com.battlegroundspvp.BattleModule;
import com.battlegroundspvp.BattleModuleLoader;
import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.enums.EventSound;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TrailRunnable implements Runnable {

    @Getter
    private static HashSet<Player> still = new HashSet<>();
    private static ArrayList<Player> disabled = new ArrayList<>();
    private BattlegroundsCore plugin;
    private Map<Player, Location> playerLocations = new HashMap<>();

    public TrailRunnable(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            playerLocations.putIfAbsent(player, player.getLocation());
            if (player.getLocation().getX() == playerLocations.get(player).getX()
                    && player.getLocation().getY() == playerLocations.get(player).getY()
                    && player.getLocation().getZ() == playerLocations.get(player).getZ()) {
                if (!still.contains(player)) {
                    playerLocations.put(player, player.getLocation());
                    still.add(player);
                    for (BattleModule module : BattleModuleLoader.modules.keySet())
                        if (module.getActiveTrail(player) != null)
                            module.getActiveTrail(player).idle.add(player);
                    AFKRunnable.getAfkTimer().put(player, 0);
                }
            } else {
                playerLocations.put(player, player.getLocation());
                still.remove(player);
                for (BattleModule module : BattleModuleLoader.modules.keySet())
                    if (module.getActiveTrail(player) != null)
                        module.getActiveTrail(player).idle.remove(player);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (BattlegroundsCore.getAfk().contains(player.getUniqueId())) {
                        if (AFKRunnable.getAfkTimer().containsKey(player)) {
                            AFKRunnable.getAfkTimer().remove(player);
                            BattlegroundsCore.clearTitle(player);
                            player.sendMessage(ChatColor.GRAY + "You are no longer AFK");
                            EventSound.playSound(player, EventSound.CLICK);
                            BattlegroundsCore.getAfk().remove(player.getUniqueId());
                            plugin.respawn(player);
                        }
                    }
                }, 2L);
            }

            if (player.getGameMode() != GameMode.CREATIVE) {
                if (!disabled.contains(player)) {
                    if (!still.contains(player)) {
                        for (BattleModule module : BattleModuleLoader.modules.keySet())
                            if (module.getActiveTrail(player) != null)
                                module.getActiveTrail(player).onMove(player);
                    }
                }
            }
        }
    }

    public void toggle(Player player) {
        if (disabled.contains(player))
            disabled.remove(player);
        else disabled.add(player);
    }
}
