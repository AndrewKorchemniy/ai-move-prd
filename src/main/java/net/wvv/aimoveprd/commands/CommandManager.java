package net.wvv.aimoveprd.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;
import net.wvv.aimoveprd.logging.IPlayerLogger;
import net.wvv.aimoveprd.player.IClientPlayersManager;

public class CommandManager {
    private final IClientPlayersManager clientPlayersManager;
    private final IPlayerLogger logger;

    public CommandManager(IClientPlayersManager clientPlayersManager, IPlayerLogger logger) {
        this.clientPlayersManager = clientPlayersManager;
        this.logger = logger;
    }

    public void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("addtracking")
                    .then(ClientCommandManager.argument("username", StringArgumentType.string())
                            .executes(context -> {
                                var username = context.getArgument("username", String.class);
                                clientPlayersManager.addTracking(username);
                                return 1;
                            })));

            dispatcher.register(ClientCommandManager.literal("removetracking")
                    .then(ClientCommandManager.argument("username", StringArgumentType.string())
                            .executes(context -> {
                                var username = context.getArgument("username", String.class);
                                clientPlayersManager.removeTracking(username);
                                return 1;
                            })));

            dispatcher.register(ClientCommandManager.literal("listtracking")
                    .executes(context -> {
                        StringBuilder playersList = new StringBuilder("Tracking players:");
                        for (var player : clientPlayersManager.getPlayers()) {
                            playersList.append(player.getDisplayName());
                        }
                        context.getSource().sendFeedback(Text.literal(playersList.toString()));
                        return 1;
                    }));

            dispatcher.register(ClientCommandManager.literal("cleartracking")
                    .executes(context -> {
                        clientPlayersManager.clearTracking();
                        return 1;
                    }));

            dispatcher.register(ClientCommandManager.literal("startlogging")
                    .executes(context -> {
                        logger.start();
                        return 1;
                    }));

            dispatcher.register(ClientCommandManager.literal("stoplogging")
                    .executes(context -> {
                        logger.stop();
                        return 1;
                    }));
        });
    }
}
