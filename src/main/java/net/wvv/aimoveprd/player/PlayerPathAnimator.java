package net.wvv.aimoveprd.player;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.Vec3d;
import net.wvv.aimoveprd.logging.IPlayerLogger;

import java.util.List;

public class PlayerPathAnimator {
    private final ClientPlayersManager clientPlayersManager;
    private IPlayerLogger logger;
    private IPlayerMovementRegressor playerMovementPredictor;
    private int pathLength = 20;

    public PlayerPathAnimator(ClientPlayersManager clientPlayersManager, IPlayerMovementRegressor playerMovementPredictor, IPlayerLogger logger) {
        this.clientPlayersManager = clientPlayersManager;
        this.playerMovementPredictor = playerMovementPredictor;
        this.logger = logger;
    }

    public void update(ClientWorld world) {
        List<Entity> players = clientPlayersManager.getPlayers();

        for (Entity player : players) {
            var playerLogs = logger.getLogs(player.getUuid().toString());

            if (playerLogs == null || playerLogs.size() < 2) {
                return;
            }

            var predictedPath = playerMovementPredictor.predict(playerLogs, pathLength);
            animatePath(predictedPath, world);
        }
    }

    public void setPredictor(IPlayerMovementRegressor playerMovementPredictor) {
        this.playerMovementPredictor = playerMovementPredictor;
    }

    public void setLogger(IPlayerLogger logger) {
        this.logger.stop();
        this.logger = logger;
    }

    public void setPathLength(int pathLength) {
        this.pathLength = pathLength;
    }

    private void animatePath(List<Vec3d> path, ClientWorld world) {
        for (int i = 0; i < path.size(); i++) {
            Vec3d pos = path.get(i);

            float red = 1.0f;
            float green = (float) Math.min(1.0, (double) i / path.size());
            var decimalColor = (int)(red * 255) << 16 | (int)(green * 255) << 8;

            DustParticleEffect effect = new DustParticleEffect(decimalColor, 1);

            world.addParticle(effect, pos.x, pos.y, pos.z, 0, 0, 0);
        }
    }
}
