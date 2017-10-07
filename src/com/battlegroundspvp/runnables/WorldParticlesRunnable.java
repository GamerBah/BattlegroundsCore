package com.battlegroundspvp.runnables;
/* Created by GamerBah on 8/30/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.packets.particles.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class WorldParticlesRunnable implements Runnable {

    private BattlegroundsCore plugin;
    private int i = 0;

    public WorldParticlesRunnable(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void run() {
        for (Location location : plugin.getFLaunchersParticle()) {
            if (Bukkit.getServer().getOnlinePlayers() != null) {
                if (i <= 10) {
                    location = location.add(0, 0, round(i / 5.5 / 10, 1));
                    ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, location, 25);
                } else if (i <= 20 && i > 10) {
                    location = location.add(round((i - 10) / 5.5 / 10, 1), 0, 0);
                    ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, location, 25);
                } else if (i <= 30 && i > 20) {
                    location = location.add(0, 0, round(-(i - 20) / 5.5 / 10, 1));
                    ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, location, 25);
                } else if (i <= 40 && i > 30) {
                    location = location.add(round(-(i - 30) / 5.5 / 10, 1), 0, 0);
                    ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, location, 25);
                } else if (i > 40) {
                    i -= 40;
                }
            }
        }
        for (Location location : plugin.getULaunchersParticle()) {
            if (Bukkit.getServer().getOnlinePlayers() != null) {
                if (i <= 10) {
                    location = location.add(0, 0, round(i / 5.5 / 10, 1));
                    ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, location, 25);
                } else if (i <= 20 && i > 10) {
                    location = location.add(round((i - 10) / 5.5 / 10, 1), 0, 0);
                    ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, location, 25);
                } else if (i <= 30 && i > 20) {
                    location = location.add(0, 0, round(-(i - 20) / 5.5 / 10, 1));
                    ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, location, 25);
                } else if (i <= 40 && i > 30) {
                    location = location.add(round(-(i - 30) / 5.5 / 10, 1), 0, 0);
                    ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 1, location, 25);
                } else if (i > 40) {
                    i -= 40;
                }
            }
        }
        i++;
    }
}
