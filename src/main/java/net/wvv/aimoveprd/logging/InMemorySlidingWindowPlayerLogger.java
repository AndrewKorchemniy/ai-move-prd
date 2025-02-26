package net.wvv.aimoveprd.logging;

import java.util.*;

public class InMemorySlidingWindowPlayerLogger implements IPlayerLogger {
    private final Map<String, List<PlayerLog>> logs;
    private int size;
    private boolean isLogging = false;

    public InMemorySlidingWindowPlayerLogger(int size) {
        this.size = size;
        this.logs = new HashMap<>();
    }

    public void log(PlayerLog log) {
        if (!isLogging) {
            return;
        }

        var playerLogs = logs.computeIfAbsent(log.uuid(), k -> new ArrayList<>());
        if (playerLogs.size() >= size) {
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

    public void setWindowSize(int size) {
        logs.clear();
        this.size = size;
    }

    public List<PlayerLog> getLogs(String playerName) {
        return logs.getOrDefault(playerName, Collections.emptyList());
    }
}
