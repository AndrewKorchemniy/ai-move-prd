package net.wvv.aimoveprd;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.wvv.aimoveprd.commands.CommandManager;
import net.wvv.aimoveprd.logging.InMemorySlidingWindowPlayerLogger;
import net.wvv.aimoveprd.player.ClientPlayersManager;
import net.wvv.aimoveprd.player.CubicPlayerMovementRegressor;
import net.wvv.aimoveprd.player.PlayerPathAnimator;

public class AimoveprdClient implements ClientModInitializer {
    private final InMemorySlidingWindowPlayerLogger defaultLogger = new InMemorySlidingWindowPlayerLogger();
    private final ClientPlayersManager clientPlayersManager = new ClientPlayersManager(defaultLogger);
    private final CubicPlayerMovementRegressor defaultMovementPredictor = new CubicPlayerMovementRegressor();
    private final PlayerPathAnimator playerPathAnimator = new PlayerPathAnimator(clientPlayersManager, defaultMovementPredictor, defaultLogger);
    private final CommandManager commandManager = new CommandManager(clientPlayersManager, defaultLogger, playerPathAnimator, defaultMovementPredictor);

    @Override
    public void onInitializeClient() {
        registerEvents();
        commandManager.registerCommands();
    }

    public void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world != null) {
                clientPlayersManager.update(client.world);
                playerPathAnimator.update(client.world);
            }
        });
    }
}
