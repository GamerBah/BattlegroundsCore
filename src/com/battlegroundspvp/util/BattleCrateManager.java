package com.battlegroundspvp.util;
/* Created by GamerBah on 6/10/2018 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.util.enums.Rarity;
import lombok.Getter;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class BattleCrateManager {

    @Getter
    public static final ArrayList<BattleCrate> crates = new ArrayList<>();
    @Getter
    private static final HashMap<UUID, Rarity> playersUsing = new HashMap<>();

    public static boolean addCrate(final BattleCrate crate) {
        if (crates.stream().noneMatch(c -> c.getId() == crate.getId()))
            return crates.add(crate);
        return false;
    }

    public static boolean removeCrate(final BattleCrate crate) {
        if (crates.stream().anyMatch(c -> c.getId() == crate.getId()))
            return crates.remove(crate);
        return false;
    }

    public static BattleCrate fromLocation(final Location location) {
        return crates.stream()
                .filter(c -> c.getLocation().hashCode() == location.hashCode())
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format("no crate found in world %s at %d - %d - %d", location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ())));
    }

    public static BattleCrate fromId(final int id) {
        return crates.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("no crate found with id %d", id)));
    }

    public static int open(final BattleCrate crate) {
        int task = BattlegroundsCore.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(BattlegroundsCore.getInstance(), () -> {
            for (Player players : Bukkit.getOnlinePlayers()) {
                PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(new BlockPosition(crate.getLocation().getX(),
                        crate.getLocation().getY(), crate.getLocation().getZ()), CraftMagicNumbers.getBlock(crate.getLocation().getBlock()), 1, 1);
                ((CraftPlayer) players).getHandle().playerConnection.sendPacket(packet);
            }
        }, 0L, 1L);
        return task;
    }

    public static boolean isUsing(final Player player) {
        return playersUsing.containsKey(player.getUniqueId());
    }

}
