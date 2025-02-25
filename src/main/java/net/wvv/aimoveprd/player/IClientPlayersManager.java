package net.wvv.aimoveprd.player;

import net.minecraft.entity.Entity;
import net.wvv.aimoveprd.logging.IPlayerLogger;

import java.util.List;

public interface IClientPlayersManager {
    void registerOnClientTick();

    void addTracking(String username);

    void removeTracking(String username);

    void clearTracking();

    List<Entity> getPlayers();

    void setLogger(IPlayerLogger logger);
}
