package net.wvv.aimoveprd.player;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class ClientPlayersManager implements IClientPlayersManager {
    private final List<String> trackingUsernames = new ArrayList<>();
    private final List<Entity> players = new ArrayList<>();

    public void registerOnClientTick() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null) {
                updatePlayers(client.world);
            }
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
