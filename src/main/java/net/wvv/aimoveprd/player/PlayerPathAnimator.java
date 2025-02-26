package net.wvv.aimoveprd.player;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.Vec3d;
import net.wvv.aimoveprd.logging.IPlayerLogger;
import net.wvv.aimoveprd.logging.PlayerLog;

import java.util.List;

public class PlayerPathAnimator {
    private final IClientPlayersManager clientPlayersManager;
    private final IPlayerLogger logger;
    private IPlayerMovementRegressor playerMovementPredictor;

    public PlayerPathAnimator(IClientPlayersManager clientPlayersManager, IPlayerMovementRegressor playerMovementPredictor, IPlayerLogger logger) {
        this.clientPlayersManager = clientPlayersManager;
        this.playerMovementPredictor = playerMovementPredictor;
        this.logger = logger;
    }

    public void update(ClientWorld world) {
        List<Entity> players = clientPlayersManager.getPlayers();

        for (Entity player : players) {
            if (player.isOnGround()) {
                return;
            }
            var playerLogs = logger.getLogs(player.getUuid().toString());
            var actualPath = playerLogs.stream().map(PlayerLog::getXYZ).toList();
            var predictedPath = playerMovementPredictor.predict(actualPath, 20);
            animatePath(predictedPath, world);
        }
    }

    public void setPredictor(IPlayerMovementRegressor playerMovementPredictor) {
        this.playerMovementPredictor = playerMovementPredictor;
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
