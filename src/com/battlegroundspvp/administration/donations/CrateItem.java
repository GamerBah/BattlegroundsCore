package com.battlegroundspvp.administration.donations;
/* Created by GamerBah on 10/12/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

public class CrateItem {

    private BattlegroundsCore plugin = BattlegroundsCore.getInstance();

    public int open(Location location) {
        int task = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player players : Bukkit.getOnlinePlayers()) {
                PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(new BlockPosition(location.getX(),
                        location.getY(), location.getZ()), CraftMagicNumbers.getBlock(location.getBlock()), 1, 1);
                ((CraftPlayer) players).getHandle().playerConnection.sendPacket(packet);
            }
        }, 0L, 1L);
        return task;
    }

    public static boolean isOpening(Player player) {
        for (Location location : BattlegroundsCore.getUsingCrates().keySet()) {
            if (BattlegroundsCore.getUsingCrates().get(location) != null)
                if (BattlegroundsCore.getUsingCrates().get(location).equals(player))
                    return true;
        }
        return false;
    }

    public static boolean isInUse(Location location) {
        return BattlegroundsCore.getUsingCrates().containsKey(location);
    }

}
