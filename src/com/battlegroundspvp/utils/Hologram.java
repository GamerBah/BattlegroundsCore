package com.battlegroundspvp.utils;
/* Created by GamerBah on 11/1/2017 */

import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hologram {

    private static final int ENTITY_ID = 7000;

    @Getter
    private int id = ENTITY_ID + 1;
    @Getter
    private Location location;
    @Getter
    private ArrayList<String> lines = new ArrayList<>();
    @Getter
    private ArrayList<String> conditional = new ArrayList<>();
    @Getter
    private ArrayList<ArmorStand> stands = new ArrayList<>();
    private List<WrapperPlayServerSpawnEntityLiving> conditionalPackets = new ArrayList<>();

    public Hologram(Location location, boolean hidden, String... lines) {
        this.location = location;
        this.lines.addAll(Arrays.asList(lines));
        this.lines.stream().filter(line -> !conditional.contains(line)).forEach(line -> {
            ArmorStand stand = (ArmorStand) this.location.getWorld().spawnEntity(this.location.clone().add(0, 0.25 * this.lines.indexOf(line), 0), EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setBasePlate(false);
            stand.setSmall(true);
            stand.setCustomName(line);
            stand.setCustomNameVisible(!hidden);
            stand.setCollidable(false);
            stand.setGravity(false);
            stand.setAI(false);
            stand.setSilent(true);
            stand.setInvulnerable(true);
            stand.setCanPickupItems(false);
            stands.add(stand);
        });
    }

    public void addConditionalLine(String line) {
        lines.add(line);
        conditional.add(line);
        ArmorStand stand = (ArmorStand) this.location.getWorld().spawnEntity(this.location.clone().add(0, 0.25 * this.lines.indexOf(line), 0), EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setBasePlate(false);
        stand.setSmall(true);
        stand.setCustomName(line);
        stand.setCustomNameVisible(true);
        stand.setCollidable(false);
        stand.setGravity(false);
        stand.setAI(false);
        stand.setSilent(true);
        stand.setInvulnerable(true);
        stand.setCanPickupItems(false);

        WrapperPlayServerSpawnEntityLiving spawnEntityPacket = new WrapperPlayServerSpawnEntityLiving(stand);
        WrappedDataWatcher.Serializer stringSerializer = WrappedDataWatcher.Registry.get(String.class);
        WrappedDataWatcher entityData = new WrappedDataWatcher(stand);
        stand.remove();

        entityData.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, stringSerializer), line);
        spawnEntityPacket.setMetadata(entityData);

        conditionalPackets.add(spawnEntityPacket);
    }

    public void updateConditionalLine(int conditionalLine, Object newData) {
        WrapperPlayServerSpawnEntityLiving spawnEntityPacket = conditionalPackets.get(conditionalLine);
        WrappedDataWatcher entityData = spawnEntityPacket.getMetadata();
        //entityData.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.get(String.class)), conditional.get(conditionalLine).replace("$data$", newData + ""));
        spawnEntityPacket.setMetadata(entityData);
    }

    public void displayConditionalLines(Player player) {
        conditionalPackets.forEach(packet -> packet.sendPacket(player));
    }

}
