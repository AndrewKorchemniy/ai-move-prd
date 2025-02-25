package net.wvv.aimoveprd.logging;

import java.util.List;

public interface IPlayerLogger {
    void log(PlayerLog log);

    void start();

    void stop();

    List<PlayerLog> getLogs(String uuid);
}
