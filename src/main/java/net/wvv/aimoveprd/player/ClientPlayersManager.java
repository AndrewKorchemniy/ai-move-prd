package net.wvv.aimoveprd.player;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.wvv.aimoveprd.logging.IPlayerLogger;
import net.wvv.aimoveprd.logging.PlayerLog;

import java.util.ArrayList;
import java.util.List;

public class ClientPlayersManager implements IClientPlayersManager {
    private final List<String> trackingUsernames = new ArrayList<>();
    private final List<Entity> players = new ArrayList<>();
    private IPlayerLogger logger;
    private int tick = 0;

    public ClientPlayersManager(IPlayerLogger logger) {
        this.logger = logger;
    }

    public void registerOnClientTick() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null) {
                updatePlayers(client.world);
                log();
            }

            tick++;
        });
    }

    public void setLogger(IPlayerLogger newLogger) {
        logger.stop();
        logger = newLogger;
        logger.start();
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

    public void addTracking(String username) {
        trackingUsernames.add(username);
    }

    public void removeTracking(String username) {
        trackingUsernames.remove(username);
    }

    public void clearTracking() {
        trackingUsernames.clear();
    }

    public List<Entity> getPlayers() {
        return players;
    }

    private void updatePlayers(ClientWorld world) {
        players.clear();

        world.getEntities().forEach(entity -> {
            if (entity.isPlayer() && !entity.isInvisible() && entity.isAlive() && trackingUsernames.contains(entity.getName().getString())) {
                players.add(entity);
            }
        });
    }
}
