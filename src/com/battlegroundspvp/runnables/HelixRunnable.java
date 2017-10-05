package com.battlegroundspvp.runnables;
/* Created by GamerBah on 8/30/2016 */

import com.battlegroundspvp.Core;

public class HelixRunnable implements Runnable {

    private double phi = 0;
    private Core plugin;

    public HelixRunnable(Core plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        /*for (Player player : plugin.getServer().getOnlinePlayers()) {
            GameProfile gameProfile = plugin.getGameProfile(player.getUniqueId());
            if (TrailRunnable.getStill().contains(player)) {
                if (!Core.getAfk().contains(player.getUniqueId())) {
                    //if (!CombatListener.getTagged().containsKey(player.getUniqueId())) {
                        if (gameProfile.getTrail().equals(Cosmetic.Item.TRAIL_FLAME_WARRIOR)) {
                            phi += Math.PI / 16;
                            double x, y, z;
                            Location location = player.getLocation();
                            for (double t = 0; t <= 2 * Math.PI; t = t + Math.PI / 12) {
                                for (double i = 0; i <= 2; i = i + 1) {
                                    x = 0.5 * (2 * Math.PI - t) * 0.375 * Math.cos(t + phi + i * Math.PI);
                                    y = 0.425 * t;
                                    z = 0.5 * (2 * Math.PI - t) * 0.375 * Math.sin(t + phi + i * Math.PI);
                                    location.add(x, y, z);
                                    ParticleEffect.FLAME.display(0, 0, 0, 0, 1, location, 25);
                                    location.subtract(x, y, z);
                                }
                            }
                        }
                    }
                }
            }*/
        //}
    }
}
