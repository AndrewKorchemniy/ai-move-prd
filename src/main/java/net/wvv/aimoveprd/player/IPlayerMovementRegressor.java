package net.wvv.aimoveprd.player;

import net.minecraft.util.math.Vec3d;

import java.util.List;

public interface IPlayerMovementRegressor {
    List<Vec3d> predict(List<Vec3d> actual, int ticks);
}
