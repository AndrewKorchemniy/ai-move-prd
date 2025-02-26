package net.wvv.aimoveprd.player;

import net.minecraft.entity.Entity;
import net.wvv.aimoveprd.logging.IPlayerLogger;

import java.util.List;

public interface IClientPlayersManager {
    void addTracking(String playerName);

    void removeTracking(String playerName);

    void clearTracking();

    List<Entity> getPlayers();

    void setLogger(IPlayerLogger logger);
}
