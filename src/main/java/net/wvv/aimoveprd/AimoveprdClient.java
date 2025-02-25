package net.wvv.aimoveprd;

import net.fabricmc.api.ClientModInitializer;
import net.wvv.aimoveprd.commands.CommandManager;
import net.wvv.aimoveprd.logging.InMemorySlidingWindowPlayerLogger;
import net.wvv.aimoveprd.player.ClientPlayersManager;
import net.wvv.aimoveprd.player.CubicFitPlayerMovementRegressor;
import net.wvv.aimoveprd.player.LinearFitPlayerMovementRegressor;
import net.wvv.aimoveprd.player.PlayerPathAnimator;

public class AimoveprdClient implements ClientModInitializer {
    //    private final FilePlayerLogger playersLogger = new FilePlayerLogger();
    private final InMemorySlidingWindowPlayerLogger playerLogger = new InMemorySlidingWindowPlayerLogger(2);
    private final ClientPlayersManager clientPlayersManager = new ClientPlayersManager(playerLogger);
    private final CommandManager commandManager = new CommandManager(clientPlayersManager, playerLogger);
    private final LinearFitPlayerMovementRegressor bestFitPlayerMovementPredictor = new LinearFitPlayerMovementRegressor();
    private final PlayerPathAnimator playerPathAnimator = new PlayerPathAnimator(clientPlayersManager, bestFitPlayerMovementPredictor, playerLogger);

    @Override
    public void onInitializeClient() {
        clientPlayersManager.registerOnClientTick();
        commandManager.registerCommands();
        playerPathAnimator.animateOnTick();
    }
}
