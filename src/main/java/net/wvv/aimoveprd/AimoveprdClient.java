package net.wvv.aimoveprd;

import net.fabricmc.api.ClientModInitializer;
import net.wvv.aimoveprd.commands.CommandManager;
import net.wvv.aimoveprd.logging.ClientPlayersLogger;
import net.wvv.aimoveprd.player.ClientPlayersManager;

public class AimoveprdClient implements ClientModInitializer {
    private final ClientPlayersManager clientPlayersManager = new ClientPlayersManager();
    private final ClientPlayersLogger playersLogger = new ClientPlayersLogger(clientPlayersManager);
    private final CommandManager commandManager = new CommandManager(clientPlayersManager, playersLogger);

    @Override
    public void onInitializeClient() {
        clientPlayersManager.registerOnClientTick();
        playersLogger.registerOnClientTick();
        commandManager.registerCommands();
    }
}
