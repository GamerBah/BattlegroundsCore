package com.battlegroundspvp.utils;
/* Created by GamerBah on 11/3/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import de.slikey.effectlib.util.ParticleEffect;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Launcher {

    @Getter
    private final int id;
    @Getter
    private final Location location;
    @Getter
    private final Type type;
    @Getter
    @Setter
    private int strength;
    @Getter
    private Location particleLocation;

    public Launcher(int id, Location location, Type type, int strength) {
        this.id = id;
        this.location = location;
        this.type = type;
        this.strength = strength;
        this.particleLocation = location.clone();
    }

    public Launcher(Location location, Type type, int strength) {
        this((BattlegroundsCore.getLaunchers().size() != 0 ? BattlegroundsCore.getLaunchers().get(BattlegroundsCore.getLaunchers().size() - 1).id + 1 : 0), location, type, strength);
    }

    public void launch(Player player) {
        if (type == Type.FORWARD) {
            player.setVelocity(location.getDirection().multiply(strength));
            //player.setVelocity(new Vector(player.getVelocity().getX(), 1.0D, player.getVelocity().getZ()));
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 1.2F, 0.3F);
            BukkitTask task = BattlegroundsCore.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(BattlegroundsCore.getInstance(),
                    () -> ParticleEffect.FIREWORKS_SPARK.display(0.1F, 0.25F, 0.1F, 0.02F, 2, player.getLocation(), 30), 0L, 1L);
            BattlegroundsCore.getInstance().getServer().getScheduler().runTaskLater(BattlegroundsCore.getInstance(), task::cancel, 30);
        }
        if (type == Type.UPWARD) {
            player.setVelocity(location.getDirection().multiply(strength));
            //player.setVelocity(new Vector(player.getVelocity().getX(), 1.0D, player.getVelocity().getZ()));
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_LAUNCH, 1.2F, 0.3F);
            BukkitTask task = BattlegroundsCore.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(BattlegroundsCore.getInstance(),
                    () -> ParticleEffect.FIREWORKS_SPARK.display(0.1F, 0.25F, 0.1F, 0.02F, 2, player.getLocation(), 30), 0L, 2L);
            BattlegroundsCore.getInstance().getServer().getScheduler().runTaskLater(BattlegroundsCore.getInstance(), task::cancel, 10);
        }
    }

    public enum Type {
        FORWARD, UPWARD
    }
}
