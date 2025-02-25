package net.wvv.aimoveprd.logging;

public interface IClientPlayersLogger
{
    void registerOnClientTick();

    void startLogging();

    void stopLogging();
}
