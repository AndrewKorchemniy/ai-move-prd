package net.wvv.aimoveprd.logging;

import org.apache.commons.lang3.NotImplementedException;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilePlayerLogger implements IPlayerLogger {
    private static final String HOME = System.getProperty("user.home");
    private static final Logger LOGGER = Logger.getLogger(FilePlayerLogger.class.getName());

    private FileHandler fh;
    private boolean isLogging = false;

    public void start() {
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

    public void stop() {
        if (!isLogging) {
            return;
        }

        LOGGER.removeHandler(fh);
        fh.close();
        isLogging = false;
    }

    public void log(PlayerLog log) {
        if (!isLogging) {
            return;
        }

        // tick, uuid, x, y, z, yaw, pitch, movement.x, movement.y, movement.z, movementDirection.x, movementDirection.y, movementDirection.z, isOnGround
        LOGGER.log(Level.INFO, String.format("%d,%s,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%b", log.tick(), log.uuid(), log.x(), log.y(), log.z(), log.yaw(), log.pitch(), log.movementX(), log.movementY(), log.movementZ(), log.movementDirectionX(), log.movementDirectionY(), log.movementDirectionZ(), log.isOnGround()));
    }

    public List<PlayerLog> getLogs(String uuid) {
        throw new NotImplementedException();
    }
}