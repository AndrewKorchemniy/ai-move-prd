package net.wvv.aimoveprd.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.text.Text;
import net.wvv.aimoveprd.logging.FilePlayerLogger;
import net.wvv.aimoveprd.logging.IPlayerLogger;
import net.wvv.aimoveprd.player.*;

import java.lang.reflect.Field;

public class CommandManager {
    private final ClientPlayersManager clientPlayersManager;
    private final PlayerPathAnimator playerPathAnimator;
    private IPlayerMovementRegressor regressor;
    private IPlayerLogger logger;

    public CommandManager(ClientPlayersManager clientPlayersManager, IPlayerLogger logger, PlayerPathAnimator playerPathAnimator, IPlayerMovementRegressor regressor) {
        this.clientPlayersManager = clientPlayersManager;
        this.logger = logger;
        this.playerPathAnimator = playerPathAnimator;
        this.regressor = regressor;
    }

    public void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

            dispatcher.register(ClientCommandManager.literal("prd")
                    .then(ClientCommandManager.literal("add")
                            .then(ClientCommandManager.argument("username", EntityArgumentType.player())
                                    .executes(context -> {
                                        var selector = context.getArgument("username", EntitySelector.class);

                                        Field playerNameField;
                                        try {
                                            playerNameField = selector.getClass().getDeclaredField("playerName");
                                            playerNameField.setAccessible(true);

                                            String playerName = (String) playerNameField.get(selector);
                                            clientPlayersManager.addTracking(playerName);

                                            context.getSource().sendFeedback(Text.literal("Added: " + playerName));
                                        } catch (NoSuchFieldException | IllegalAccessException e) {
                                            context.getSource().sendFeedback(Text.literal("Error: " + e.getMessage()));
                                        }

                                        return 1;
                                    }))));

            dispatcher.register(ClientCommandManager.literal("prd")
                    .then(ClientCommandManager.literal("remove")
                            .then(ClientCommandManager.argument("username", EntityArgumentType.player())
                                    .executes(context -> {
                                        var selector = context.getArgument("username", EntitySelector.class);

                                        Field playerNameField;
                                        try {
                                            playerNameField = selector.getClass().getDeclaredField("playerName");
                                            playerNameField.setAccessible(true);

                                            String playerName = (String) playerNameField.get(selector);
                                            clientPlayersManager.removeTracking(playerName);

                                            context.getSource().sendFeedback(Text.literal("Removed: " + playerName));
                                        } catch (NoSuchFieldException | IllegalAccessException e) {
                                            context.getSource().sendFeedback(Text.literal("Error: " + e.getMessage()));
                                        }

                                        return 1;
                                    }))));

            dispatcher.register(ClientCommandManager.literal("prd")
                    .then(ClientCommandManager.literal("clear")
                            .executes(context -> {
                                clientPlayersManager.clearTracking();
                                return 1;
                            })));

            dispatcher.register(ClientCommandManager.literal("prd")
                    .then(ClientCommandManager.literal("list")
                            .executes(context -> {
                                StringBuilder playersList = new StringBuilder("Tracking players: ");
                                for (var player : clientPlayersManager.getPlayers()) {
                                    playersList.append(player.getName().getString()).append(", ");
                                }
                                context.getSource().sendFeedback(Text.literal(playersList.toString()));
                                return 1;
                            })));

            dispatcher.register(ClientCommandManager.literal("prd")
                    .then(ClientCommandManager.literal("config")
                            .then(ClientCommandManager.argument("key", new ConfigArgumentType())
                                    .then(ClientCommandManager.argument("value", StringArgumentType.string())
                                            .executes(context -> {
                                                ConfigArgument config = context.getArgument("key", ConfigArgument.class);
                                                String value = context.getArgument("value", String.class);

                                                switch (config.type) {
                                                    case PREDICTOR:
                                                        switch (value) {
                                                            case "linear" -> {
                                                                regressor = new LinearPlayerMovementRegressor();
                                                                playerPathAnimator.setPredictor(regressor);
                                                                context.getSource().sendFeedback(Text.literal("Set movement predictor to: linear"));
                                                            }
                                                            case "cubic" -> {
                                                                regressor = new CubicPlayerMovementRegressor();
                                                                playerPathAnimator.setPredictor(regressor);
                                                                context.getSource().sendFeedback(Text.literal("Set movement predictor to: cubic"));
                                                            }
                                                            case "perceptron" -> {
                                                                regressor = new PerceptronPlayerMovementRegressor();
                                                                playerPathAnimator.setPredictor(regressor);
                                                                context.getSource().sendFeedback(Text.literal("Set movement predictor to: perceptron"));
                                                            }
                                                            default ->
                                                                    context.getSource().sendFeedback(Text.literal("Invalid predictor type: " + value));
                                                        }
                                                        break;
                                                    case WINDOW_SIZE:
                                                        int windowSize = Integer.parseInt(value);

                                                        if (windowSize < 2) {
                                                            context.getSource().sendFeedback(Text.literal("Window size must be greater than 1"));
                                                            break;
                                                        }

                                                        regressor.setWindowSize(windowSize);
                                                        context.getSource().sendFeedback(Text.literal("Set window size to: " + windowSize));
                                                        break;
                                                    case LOGGER:
                                                        if (value.equals("file")) {
                                                            this.logger = new FilePlayerLogger();
                                                            clientPlayersManager.setLogger(this.logger);
                                                            playerPathAnimator.setLogger(this.logger);
                                                            context.getSource().sendFeedback(Text.literal("Set logger to: file"));
                                                        } else if (value.equals("memory")) {
                                                            clientPlayersManager.setLogger(this.logger);
                                                            playerPathAnimator.setLogger(this.logger);
                                                            context.getSource().sendFeedback(Text.literal("Set logger to: memory"));
                                                        } else {
                                                            context.getSource().sendFeedback(Text.literal("Invalid logger type: " + value));
                                                        }
                                                        break;
                                                    case PATH_LENGTH:
                                                        int pathLength = Integer.parseInt(value);

                                                        if (pathLength < 2) {
                                                            context.getSource().sendFeedback(Text.literal("Path length must be greater than 1"));
                                                            break;
                                                        }

                                                        playerPathAnimator.setPathLength(pathLength);
                                                        context.getSource().sendFeedback(Text.literal("Set path length to: " + pathLength));
                                                        break;
                                                    default:
                                                        break;
                                                }

                                                return 1;
                                            })))));

            dispatcher.register(ClientCommandManager.literal("prd")
                    .then(ClientCommandManager.literal("start")
                            .executes(context -> {
                                clientPlayersManager.setLogger(logger);
                                logger.start();
                                context.getSource().sendFeedback(Text.literal("Started logging."));
                                return 1;
                            })));

            dispatcher.register(ClientCommandManager.literal("prd")
                    .then(ClientCommandManager.literal("stop")
                            .executes(context -> {
                                logger.stop();
                                context.getSource().sendFeedback(Text.literal("Stopped logging."));
                                return 1;
                            })));
        });
    }
}
