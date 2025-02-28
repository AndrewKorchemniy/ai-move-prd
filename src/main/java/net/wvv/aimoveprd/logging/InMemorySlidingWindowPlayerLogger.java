package net.wvv.aimoveprd.logging;

import java.util.*;

public class InMemorySlidingWindowPlayerLogger implements IPlayerLogger {
    private final Map<String, List<PlayerLog>> logs;
    private boolean isLogging = false;

    public InMemorySlidingWindowPlayerLogger() {
        this.logs = new HashMap<>();
    }

    public void log(PlayerLog log) {
        if (!isLogging) {
            return;
        }

        var playerLogs = logs.computeIfAbsent(log.uuid(), k -> new ArrayList<>());
        if (playerLogs.size() >= 20) {
            playerLogs.removeFirst();
        }

        playerLogs.add(log);
    }

    public void start() {
        if (isLogging) {
            return;
        }

        logs.clear();
        isLogging = true;
    }

    public void stop() {
        if (!isLogging) {
            return;
        }

        logs.clear();
        isLogging = false;
    }

    public List<PlayerLog> getLogs(String playerName) {
        return logs.getOrDefault(playerName, Collections.emptyList());
    }
}
