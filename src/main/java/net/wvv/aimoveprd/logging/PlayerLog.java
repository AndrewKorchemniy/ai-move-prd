package net.wvv.aimoveprd.logging;

import net.minecraft.util.math.Vec3d;

public record PlayerLog(long tick, String uuid, double x, double y, double z, float yaw, float pitch, double movementX,
                        double movementY, double movementZ, double movementDirectionX, double movementDirectionY,
                        double movementDirectionZ, boolean isOnGround) {
    public Vec3d getXYZ() {
        return new Vec3d(x, y, z);
    }
}