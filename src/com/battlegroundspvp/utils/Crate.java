package com.battlegroundspvp.utils;
/* Created by GamerBah on 11/2/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.utils.messages.ColorBuilder;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.util.EulerAngle;

public class Crate {

    @Getter
    private final int id;
    @Getter
    private final Location location;
    @Getter
    private final Hologram hologram;
    @Getter
    private Location itemInHandLocation, blockInHandLocation, toolInHandLocation;
    @Getter
    private ArmorStand itemStand, blockStand, toolStand;

    public Crate(final int id, final Location location) {
        this.id = id;
        this.location = location;
        this.hologram = new Hologram(location.clone().add(0.5, 0.25, 0.5), false,
                new ColorBuilder(ChatColor.YELLOW).bold().create() + "RIGHT CLICK",
                new ColorBuilder(ChatColor.LIGHT_PURPLE).bold().create() + "BATTLE CRATES");

        RelativeDirection itemDirection = new RelativeDirection(location);

        this.itemInHandLocation = itemDirection.towards(RelativeDirection.Rotation.FRONT, 0.28)
                .towards(RelativeDirection.Rotation.LEFT, 0.2).getLocation().add(0, -0.65, 0);
        this.itemInHandLocation.setYaw(itemDirection.getRotationalYaw(RelativeDirection.Rotation.BEHIND));

        this.itemStand = (ArmorStand) location.getWorld().spawnEntity(itemInHandLocation, EntityType.ARMOR_STAND);
        itemStand.setVisible(false);
        itemStand.setSmall(true);
        itemStand.setCanPickupItems(false);
        itemStand.setInvulnerable(true);
        itemStand.setCollidable(false);
        itemStand.setAI(false);
        itemStand.setGravity(false);
        itemStand.setSilent(true);
        itemStand.setArms(true);
        itemStand.setBasePlate(false);
        itemStand.setRightArmPose(new EulerAngle(Math.toRadians(270), 0, 0));
        BattlegroundsCore.getEntities().add(itemStand);

        RelativeDirection blockDirection = new RelativeDirection(location);

        this.blockInHandLocation = blockDirection.towards(RelativeDirection.Rotation.FRONT, 0.2)
                .towards(RelativeDirection.Rotation.RIGHT, 0.225).getLocation().add(0, -0.25, 0);
        this.blockInHandLocation.setYaw(blockDirection.getRotationalYaw(RelativeDirection.Rotation.LEFT));

        this.blockStand = (ArmorStand) location.getWorld().spawnEntity(blockInHandLocation, EntityType.ARMOR_STAND);
        blockStand.setVisible(false);
        blockStand.setSmall(true);
        blockStand.setCanPickupItems(false);
        blockStand.setInvulnerable(true);
        blockStand.setCollidable(false);
        blockStand.setAI(false);
        blockStand.setGravity(false);
        blockStand.setSilent(true);
        blockStand.setArms(true);
        blockStand.setBasePlate(false);
        blockStand.setRightArmPose(new EulerAngle(Math.toRadians(345), 0, 0));
        BattlegroundsCore.getEntities().add(blockStand);

        RelativeDirection toolDirection = new RelativeDirection(location);

        this.toolInHandLocation = toolDirection.towards(RelativeDirection.Rotation.FRONT, 0.2)
                .towards(RelativeDirection.Rotation.RIGHT, 0.35).getLocation().add(0, -0.5, 0);
        this.toolInHandLocation.setYaw(toolDirection.getRotationalYaw(RelativeDirection.Rotation.LEFT));

        this.toolStand = (ArmorStand) location.getWorld().spawnEntity(toolInHandLocation, EntityType.ARMOR_STAND);
        toolStand.setVisible(false);
        toolStand.setSmall(true);
        toolStand.setCanPickupItems(false);
        toolStand.setInvulnerable(true);
        toolStand.setCollidable(false);
        toolStand.setAI(false);
        toolStand.setGravity(false);
        toolStand.setSilent(true);
        toolStand.setArms(true);
        toolStand.setBasePlate(false);
        toolStand.setRightArmPose(new EulerAngle(Math.toRadians(295), 0, 0));
        BattlegroundsCore.getEntities().add(toolStand);
    }

    public Crate(final Location location) {
        this((BattlegroundsCore.getCrates().size() != 0 ? BattlegroundsCore.getCrates().get(BattlegroundsCore.getCrates().size() - 1).id + 1 : 0), location);
    }

    public static Crate fromLocation(Location location) {
        for (Crate crate : BattlegroundsCore.getCrates())
            if (crate.getLocation().hashCode() == location.hashCode()) return crate;
        return null;
    }

    public static Crate fromId(int id) {
        for (Crate crate : BattlegroundsCore.getCrates())
            if (crate.getId() == id) return crate;
        return null;
    }

}
