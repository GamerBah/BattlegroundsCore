package com.battlegroundspvp.utils;
/* Created by GamerBah on 11/7/2017 */

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.util.Vector;

import static com.battlegroundspvp.utils.RelativeDirection.Rotation.DOWN;
import static com.battlegroundspvp.utils.RelativeDirection.Rotation.UP;

public class RelativeDirection {

    // NORTH: -Z
    // SOUTH: +Z
    // EAST: +X
    // WEST: -X
    // UP: +Y
    // DOWN: -Y

    @Getter
    private final Location location;
    @Getter
    private final BlockFace direction;
    @Getter
    private float baseYaw;
    @Getter
    @Setter
    private Rotation rotation;

    public RelativeDirection(Location location) {
        this.location = location.clone().add(0.5, 0, 0.5);
        if (location.getBlock() == null || !(location.getBlock().getState().getData() instanceof DirectionalContainer))
            throw new IllegalArgumentException("Block must be a directional container!");
        this.direction = ((DirectionalContainer) location.getBlock().getState().getData()).getFacing();
        if (direction == BlockFace.NORTH) this.baseYaw = 180F;
        if (direction == BlockFace.SOUTH) this.baseYaw = 0F;
        if (direction == BlockFace.WEST) this.baseYaw = 90F;
        if (direction == BlockFace.EAST) this.baseYaw = -90F;
    }

    public RelativeDirection towards(Rotation rotation, double change) {
        double y = 0;
        double x = 0;
        double z = 0;
        if (direction == BlockFace.NORTH) {
            if (rotation == Rotation.FRONT) z = -change;
            if (rotation == Rotation.BEHIND) z = change;
            if (rotation == Rotation.LEFT) x = change;
            if (rotation == Rotation.RIGHT) x = -change;
        }
        if (direction == BlockFace.SOUTH) {
            if (rotation == Rotation.FRONT) z = change;
            if (rotation == Rotation.BEHIND) z = -change;
            if (rotation == Rotation.LEFT) x = -change;
            if (rotation == Rotation.RIGHT) x = change;
        }
        if (direction == BlockFace.WEST) {
            if (rotation == Rotation.FRONT) x = -change;
            if (rotation == Rotation.BEHIND) x = change;
            if (rotation == Rotation.LEFT) z = -change;
            if (rotation == Rotation.RIGHT) z = change;
        }
        if (direction == BlockFace.EAST) {
            if (rotation == Rotation.FRONT) x = change;
            if (rotation == Rotation.BEHIND) x = -change;
            if (rotation == Rotation.LEFT) z = change;
            if (rotation == Rotation.RIGHT) z = -change;
        }
        if (rotation == UP) y = change;
        if (rotation == DOWN) y = -change;

        location.add(new Vector(x, y, z));
        this.rotation = rotation;
        return this;
    }

    public float getRotationalYaw(Rotation rotation) {
        float yaw = baseYaw;
        if (rotation == Rotation.FRONT) return yaw;
        if (rotation == Rotation.BEHIND) yaw -= 180;
        if (rotation == Rotation.LEFT) yaw += 90;
        if (rotation == Rotation.RIGHT) yaw -= 90;
        return yaw;
    }

    public enum Rotation {
        FRONT, RIGHT, LEFT, BEHIND, UP, DOWN
    }
}
