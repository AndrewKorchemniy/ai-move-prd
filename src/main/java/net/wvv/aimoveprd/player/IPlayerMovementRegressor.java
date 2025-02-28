package net.wvv.aimoveprd.player;

import net.minecraft.util.math.Vec3d;
import net.wvv.aimoveprd.logging.PlayerLog;

import java.util.List;

public interface IPlayerMovementRegressor {
    void setWindowSize(int size);
    List<Vec3d> predict(List<PlayerLog> actual, int ticks);
}
