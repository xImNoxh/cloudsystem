package de.polocloud.bootstrap.commands.version2;

import com.google.inject.Inject;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerCopyPacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.template.TemplateType;
import de.polocloud.api.util.Snowflake;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.WrapperClient;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@CloudCommand.Info(name = "gameserver", description = "Manage a Gameserver", aliases = "gs", commandType = CommandType.CONSOLE)
public class GameServerCommand extends CloudCommand {

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private ITemplateService templateService;

    @Inject
    private IWrapperClientManager wrapperClientManager;

    @Inject
    private Snowflake snowflake;

    public GameServerCommand() {
    }

    @Override
    public void execute(ICommandExecutor sender, String[] args) {
        try {
            if (args.length == 3) {
                if (args[1].equalsIgnoreCase("stop") || args[1].equalsIgnoreCase("shutdown")) {
                    String name = args[2];
                    IGameServer gameServer = gameServerManager.getGameServerByName(name).get();

                    if (gameServer == null) {
                        Logger.log(LoggerType.WARNING, Logger.PREFIX + "The gameserver » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + name + ConsoleColors.GRAY.getAnsiCode() + " isn't online!");
                        return;
                    } else {
                        gameServer.stop();
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "You " + ConsoleColors.GREEN.getAnsiCode() + "successfully " + ConsoleColors.GRAY.getAnsiCode() + "stopped the gameserver » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + name + ConsoleColors.GRAY.getAnsiCode() + "!");
                    }
                    return;
                } else if (args[1].equalsIgnoreCase("start")) {
                    String templateName = args[2];
                    ITemplate template = templateService.getTemplateByName(templateName).get();
                    if (template == null) {
                        Logger.log(LoggerType.WARNING, Logger.PREFIX + "The template » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + templateName + ConsoleColors.GRAY.getAnsiCode() + " doesn't exists!");
                        return;
                    } else {
                        int size = gameServerManager.getGameServersByTemplate(template).get().size();
                        if (size >= template.getMaxServerCount()) {
                            Logger.log(LoggerType.ERROR, Logger.PREFIX + "Cannot start the server, the maximal server online count of » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + template.getMaxServerCount()
                                + ConsoleColors.GRAY.getAnsiCode() + " was reached! (Online » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + size + ConsoleColors.GRAY.getAnsiCode() + ")");
                            return;
                        }

                        Optional<WrapperClient> optionalWrapperClient = this.wrapperClientManager.getWrapperClients().stream().findAny();

                        if (!optionalWrapperClient.isPresent()) {
                            Logger.log(LoggerType.ERROR, Logger.PREFIX + "No available Wrapper connected!");
                            return;
                        }

                        WrapperClient wrapperClient = optionalWrapperClient.get();

                        SimpleGameServer newGameServer = new SimpleGameServer(template.getName() + "-" + searchForAvailableID(template),
                            GameServerStatus.PENDING, null, snowflake.nextId(), template, System.currentTimeMillis(), template.getMotd(), template.getMaxPlayers());
                        gameServerManager.registerGameServer(newGameServer);
                        wrapperClient.startServer(newGameServer);
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "Requesting start...");
                    }

                } else if (args[1].equalsIgnoreCase("info")) {
                    String name = args[2];
                    IGameServer gameServer = gameServerManager.getGameServerByName(name).get();
                    if (gameServer == null) {
                        Logger.log(LoggerType.WARNING, Logger.PREFIX + "The gameserver » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + name + ConsoleColors.GRAY.getAnsiCode() + " isn't online!");
                        return;
                    }

                    Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Information]----");
                    Logger.newLine();
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Gameserver » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getName());
                    Logger.newLine();
                    Logger.log(LoggerType.INFO,
                        Logger.PREFIX + "Total memory » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getTotalMemory() + "mb");
                    Logger.newLine();
                    Logger.log(LoggerType.INFO,
                        Logger.PREFIX + "Id » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "#" + gameServer.getSnowflake());
                    Logger.newLine();
                    Logger.log(LoggerType.INFO,
                        Logger.PREFIX + "Status » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getStatus());
                    Logger.newLine();
                    Logger.log(LoggerType.INFO,
                        Logger.PREFIX + "Started time » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getStartTime());
                    Logger.newLine();
                    Logger.log(LoggerType.INFO,
                        Logger.PREFIX + "Ping » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getPing() + "ms");
                    Logger.newLine();
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "----[/Information]----");
                    return;
                } else {
                    sendHelp();
                }
            } else if (args.length == 4) {
                if (args[1].equalsIgnoreCase("start")) {
                    String templateName = args[2];
                    ITemplate template = templateService.getTemplateByName(templateName).get();
                    if (template == null) {
                        Logger.log(LoggerType.WARNING, Logger.PREFIX + "The template » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + templateName + ConsoleColors.GRAY.getAnsiCode() + " doesn't exists!");
                    } else {
                        String amountString = args[3];
                        int amount;
                        try {
                            amount = Integer.parseInt(amountString);
                        } catch (NumberFormatException exception) {
                            Logger.log(LoggerType.ERROR, Logger.PREFIX + "Please provide a real number (int)");
                            return;
                        }

                        if (amount < 0) {
                            Logger.log(LoggerType.ERROR, Logger.PREFIX + "You cannot start " + amountString + " servers!");
                            return;
                        }
                        int size = gameServerManager.getGameServersByTemplate(template).get().size();
                        if ((size + amount) >= template.getMaxServerCount()) {
                            Logger.log(LoggerType.ERROR, Logger.PREFIX + "Cannot start the server, the maximal server online count of » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + template.getMaxServerCount()
                                + ConsoleColors.GRAY.getAnsiCode() + " was reached! (With new servers » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + (size + amount) + ConsoleColors.GRAY.getAnsiCode() + ")");
                            return;
                        }

                        Optional<WrapperClient> optionalWrapperClient = this.wrapperClientManager.getWrapperClients().stream().findAny();

                        if (!optionalWrapperClient.isPresent()) {
                            Logger.log(LoggerType.ERROR, Logger.PREFIX + "No available Wrapper connected!");
                            return;
                        }

                        WrapperClient wrapperClient = optionalWrapperClient.get();

                        for (int i = 0; i < amount; i++) {
                            SimpleGameServer newGameServer = new SimpleGameServer(template.getName() + "-" + searchForAvailableID(template),
                                GameServerStatus.PENDING, null, snowflake.nextId(), template, System.currentTimeMillis(), template.getMotd(), template.getMaxPlayers());
                            gameServerManager.registerGameServer(newGameServer);
                            wrapperClient.startServer(newGameServer);
                            Logger.log(LoggerType.INFO, Logger.PREFIX + "Requesting start...");
                        }
                        Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN.getAnsiCode() + "Successfully requested start for » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + amount + ConsoleColors.GRAY.getAnsiCode() + " servers!");
                    }
                } else if (args[1].equalsIgnoreCase("copy")) {
                    String name = args[2];
                    String type = args[3];
                    IGameServer gameServer = gameServerManager.getGameServerByName(name).get();
                    if (gameServer == null) {
                        Logger.log(LoggerType.WARNING, Logger.PREFIX + "The gameserver » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + name + ConsoleColors.GRAY.getAnsiCode() + " isn't online!");
                        return;
                    }
                    if (!gameServer.getStatus().equals(GameServerStatus.RUNNING)) {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "The gameserver » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + name + ConsoleColors.GRAY.getAnsiCode() + " isn't completely stopped or started!");
                        return;
                    }
                    if (type.equalsIgnoreCase("worlds")) {
                        if (gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY)) {
                            Logger.log(LoggerType.INFO, Logger.PREFIX + "A Proxy Server doesn't have worlds, so its not compatibly with the 'worlds' mode, please use the 'entire' type!");
                            return;
                        }
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "Copying " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getName() + ConsoleColors.GRAY.getAnsiCode() + "...");
                        List<WrapperClient> wrappers = Master.getInstance().getWrapperClientManager().getWrapperClients().stream().filter(wrapperClient -> Arrays.asList(gameServer.getTemplate().getWrapperNames()).contains(wrapperClient.getName())).collect(Collectors.toList());
                        for (WrapperClient wrapper : wrappers) {
                            wrapper.sendPacket(new APIRequestGameServerCopyPacket(APIRequestGameServerCopyPacket.Type.WORLD, gameServer.getName(), String.valueOf(gameServer.getSnowflake()), gameServer.getTemplate().getName()));
                        }
                    } else if (type.equalsIgnoreCase("entire")) {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "Copying " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + gameServer.getName() + ConsoleColors.GRAY.getAnsiCode() + "...");
                        List<WrapperClient> wrappers = Master.getInstance().getWrapperClientManager().getWrapperClients().stream().filter(wrapperClient -> Arrays.asList(gameServer.getTemplate().getWrapperNames()).contains(wrapperClient.getName())).collect(Collectors.toList());
                        for (WrapperClient wrapper : wrappers) {
                            wrapper.sendPacket(new APIRequestGameServerCopyPacket(APIRequestGameServerCopyPacket.Type.ENTIRE, gameServer.getName(), String.valueOf(gameServer.getSnowflake()), gameServer.getTemplate().getName()));
                        }
                    } else {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use following command: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() +
                            "gameserver copy <gameserver> entire/worlds");
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "Explanation: \n" +
                            Logger.PREFIX + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "entire: " + ConsoleColors.GRAY.getAnsiCode() + "copies the entire GameServer to the template\n" +
                            Logger.PREFIX + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "worlds: " + ConsoleColors.GRAY.getAnsiCode() + "only copies the worlds of the GameServer to the template");
                    }
                } else {
                    sendHelp();
                }
            } else {
                sendHelp();
            }
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
        }
    }

    private void sendHelp() {
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Gameserver]----");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "gameserver stop/shutdown <server> " + ConsoleColors.GRAY.getAnsiCode() + "to shutdown a gameserver");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "gameserver start <template> " + ConsoleColors.GRAY.getAnsiCode() + "to start a new gameserver");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "gameserver start <server> <amount> " + ConsoleColors.GRAY.getAnsiCode() + "to start multiple new gameservers at once");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "gameserver copy <server> worlds/entire " + ConsoleColors.GRAY.getAnsiCode() + "to copy the temproy file of a server into its template");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "gameserver info <server> " + ConsoleColors.GRAY.getAnsiCode() + "to get information of a gameserver");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[/Gameserver]----");
    }

    private int searchForAvailableID(ITemplate template) throws ExecutionException, InterruptedException {
        return gameServerManager.getGameServersByTemplate(template).get().size() + 1;
    }
}
