package net.wvv.aimoveprd.player;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.wvv.aimoveprd.logging.IPlayerLogger;
import net.wvv.aimoveprd.logging.PlayerLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ClientPlayersManager implements IClientPlayersManager {
    private final HashSet<String> trackingPlayers = new HashSet<>();
    private final List<Entity> players = new ArrayList<>();
    private IPlayerLogger logger;
    private int tick = 0;

    public ClientPlayersManager(IPlayerLogger logger) {
        this.logger = logger;
    }

    public void update(ClientWorld world) {
        updatePlayers(world);
        log();
        tick++;
    }

    public void setLogger(IPlayerLogger newLogger) {
        logger.stop();
        logger = newLogger;
    }

    private void log() {
        players.forEach(player -> {
            var uuid = player.getUuid().toString();
            var x = player.getX();
            var y = player.getY();
            var z = player.getZ();
            var yaw = player.getYaw();
            var pitch = player.getPitch();
            var movement = player.getMovement();
            var movementDirection = player.getMovementDirection().getDoubleVector();
            var isOnGround = player.isOnGround();

            var log = new PlayerLog(tick, uuid, x, y, z, yaw, pitch, movement.x, movement.y, movement.z, movementDirection.x, movementDirection.y, movementDirection.z, isOnGround);
            logger.log(log);
        });
    }

    public void addTracking(String playerName) {
        trackingPlayers.add(playerName);
    }

    public void removeTracking(String playerName) {
        trackingPlayers.remove(playerName);
    }

    public void clearTracking() {
        trackingPlayers.clear();
    }

    public List<Entity> getPlayers() {
        return players;
    }

    private void updatePlayers(ClientWorld world) {
        players.clear();

        world.getEntities().forEach(entity -> {
            if (entity.isPlayer() && !entity.isInvisible() && entity.isAlive() && entity.getDisplayName() != null && trackingPlayers.contains(entity.getDisplayName().getString())) {
                players.add(entity);
            }
        });
    }
}
