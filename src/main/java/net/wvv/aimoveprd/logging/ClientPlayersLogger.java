package net.wvv.aimoveprd.logging;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.wvv.aimoveprd.player.IClientPlayersManager;

import java.io.File;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientPlayersLogger implements IClientPlayersLogger {
    private static final String HOME = System.getProperty("user.home");
    private static final Logger LOGGER = Logger.getLogger(ClientPlayersLogger.class.getName());

    private final IClientPlayersManager manager;
    private FileHandler fh;
    private boolean isLogging = false;
    private long tick = 0;

    public ClientPlayersLogger(IClientPlayersManager manager) {
        this.manager = manager;
    }

    public void registerOnClientTick() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            log();
            tick++;
        });
    }

    public void startLogging() {
        if (isLogging) {
            return;
        }

        try {
            var logsDir = new File(HOME + "/logs");
            if (!logsDir.exists()) {
                var res = logsDir.mkdirs();
                if (!res) {
                    LOGGER.log(Level.SEVERE, "Failed to create logs directory");
                    return;
                }
            }

            fh = new FileHandler(HOME + "/logs/" + UUID.randomUUID() + ".csv");
            fh.setFormatter(new SimpleMessageFormatter());

            LOGGER.addHandler(fh);
            LOGGER.setLevel(Level.ALL);
            LOGGER.log(Level.INFO, "tick,uuid,x,y,z,yaw,pitch,movement.x,movement.y,movement.z,movementDirection.x,movementDirection.y,movementDirection.z,isOnGround");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An exception occurred", e);
        }

        isLogging = true;
    }

    public void stopLogging() {
        if (!isLogging) {
            return;
        }

        LOGGER.removeHandler(fh);
        fh.close();
        isLogging = false;
    }

    private void log() {
        if (!isLogging) {
            return;
        }

        var playersInAir = manager.getPlayers();
        playersInAir.forEach(entity -> {
            var uuid = entity.getUuid().toString();
            var x = entity.getX();
            var y = entity.getY();
            var z = entity.getZ();
            var yaw = entity.getYaw();
            var pitch = entity.getPitch();
            var movement = entity.getMovement();
            var movementDirection = entity.getMovementDirection().getDoubleVector();
            var isOnGround = entity.isOnGround();

            // tick, uuid, x, y, z, yaw, pitch, movement.x, movement.y, movement.z, movementDirection.x, movementDirection.y, movementDirection.z
            LOGGER.log(Level.INFO, String.format("%d,%s,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%b", tick, uuid, x, y, z, yaw, pitch, movement.x, movement.y, movement.z, movementDirection.x, movementDirection.y, movementDirection.z, isOnGround));
        });
    }
}