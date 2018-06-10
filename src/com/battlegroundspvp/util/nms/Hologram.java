package com.battlegroundspvp.util.nms;
/* Created by GamerBah on 11/1/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import lombok.Getter;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Hologram {

    private static final int ENTITY_ID = 7000;

    @Getter
    private int id = ENTITY_ID + 1;
    @Getter
    private Location location;
    @Getter
    private ArrayList<String> lines = new ArrayList<>();
    @Getter
    private ArrayList<ArmorStand> stands = new ArrayList<>();
    @Getter
    private HashMap<HologramData, Integer> nmsData = new HashMap<>();

    public Hologram(Location location, boolean hidden, String... lines) {
        this.location = location;
        this.lines.addAll(Arrays.asList(lines));
        this.lines.forEach(line -> {
            ArmorStand stand = (ArmorStand) this.location.getWorld().spawnEntity(this.location.clone().add(0, 0.25 * this.lines.indexOf(line), 0), EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setBasePlate(false);
            stand.setSmall(true);
            stand.setCustomName(line);
            stand.setCustomNameVisible(!hidden && !line.isEmpty());
            stand.setCollidable(false);
            stand.setGravity(false);
            stand.setAI(false);
            stand.setSilent(true);
            stand.setInvulnerable(true);
            stand.setCanPickupItems(false);
            stands.add(stand);
        });
        BattlegroundsCore.getHolograms().add(this);
    }

    public void remove() {
        this.stands.forEach(Entity::remove);
    }

    public Hologram addNMS(HologramData hologramData, int heightOffset) {
        this.nmsData.put(hologramData, heightOffset);
        return this;
    }

    public Hologram addNMS(HologramData hologramData) {
        return addNMS(hologramData, 0);
    }

    public void displayNMS(Player player) {
        EntityArmorStand stand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle());
        nmsData.forEach((data, offset) -> {
            final String text = data.getData(player);
            stand.setLocation(this.location.clone().getX(), this.location.clone().getY() + (0.25 * (this.lines.size() + offset)), this.location.clone().getZ(),
                    this.location.getYaw(), this.location.getPitch());
            stand.setInvisible(true);
            stand.setBasePlate(false);
            stand.setSmall(true);
            stand.setCustomName(text);
            stand.setCustomNameVisible(!text.isEmpty());
            stand.setNoGravity(true);
            stand.setSilent(true);
            stand.setInvulnerable(true);

            new WrapperPlayServerSpawnEntityLiving(stand.getBukkitEntity()).sendPacket(player);
        });
    }

    /*public void updateConditionalLine(int conditionalLine, Object newData) {
        WrapperPlayServerSpawnEntityLiving spawnEntityPacket = packets.get(conditionalLine);
        WrappedDataWatcher entityData = spawnEntityPacket.getMetadata();
        //entityData.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.get(String.class)), nms.get(conditionalLine).replace("$data$", newData + ""));
        spawnEntityPacket.setMetadata(entityData);
    }*/

}
