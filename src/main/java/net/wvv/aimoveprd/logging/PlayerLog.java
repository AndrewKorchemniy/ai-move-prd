package net.wvv.aimoveprd.logging;

import net.minecraft.util.math.Vec3d;

import java.util.List;

public record PlayerLog(long tick, String uuid, double x, double y, double z, float yaw, float pitch, double movementX, double movementY, double movementZ, double movementDirectionX, double movementDirectionY, double movementDirectionZ, boolean isOnGround) {
    public List<Vec3d> getXYZ() {
        return List.of(new Vec3d(x, y, z));
    }
}