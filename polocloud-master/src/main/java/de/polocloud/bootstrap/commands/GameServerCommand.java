package de.polocloud.bootstrap.commands;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Arguments;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerCopyPacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.template.TemplateType;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.IWrapper;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class GameServerCommand implements CommandListener {

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private ITemplateService templateService;

    @Inject
    private Snowflake snowflake;

    public GameServerCommand() {
    }

    @Command(name = "gameserver", description = "Manage a Gameserver", aliases = "gs")
    public void execute(CommandExecutor sender, String[] rawArgs,
                        @Arguments(onlyFirstArgs =
                            {"stop", "shutdown", "start", "copy", "info", "execute"},
                            min = 2, max = 3, message =
                            {"----[Gameserver]----",
                                "Use §3gameserver stop/shutdown <server> §7to shutdown a gameserver",
                                "Use §3gameserver start <template> §7to start a new gameserver",
                                "Use §3gameserver start <server> <amount> §7to start multiple new gameservers at once",
                                "Use §3gameserver copy <server> worlds/entire §7to copy the temp files of a server into its template",
                                "Use §3gameserver info <server> §7to get information of a gameserver",
                                "Use §3gameserver execute <server> <command> §7to execute a command on a gameserver",
                                "----[Gameserver]----"}
                        ) String... args) {
        try {
            IWrapperManager wrapperManager = PoloCloudAPI.getInstance().getWrapperManager();
            if (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("shutdown")) {
                String name = args[1];
                IGameServer gameServer = gameServerManager.getGameServerByName(name).get();

                if (gameServer == null) {
                    Logger.log(LoggerType.WARNING, Logger.PREFIX + "The gameserver » " + ConsoleColors.LIGHT_BLUE + name + ConsoleColors.GRAY + " isn't online!");
                } else {
                    if (gameServer.getStatus() == GameServerStatus.RUNNING) {
                        gameServer.stop();
                    } else {
                        gameServer.terminate();
                        gameServerManager.unregisterGameServer(gameServer);
                        Logger.log(LoggerType.WARNING, "Server is not Running... Terminating Process");
                    }
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "You " + ConsoleColors.GREEN + "successfully " + ConsoleColors.GRAY + "stopped the gameserver » " + ConsoleColors.LIGHT_BLUE + name + ConsoleColors.GRAY + "!");
                }
            } else if (args[0].equalsIgnoreCase("start") && args.length == 2) {
                String templateName = args[1];
                ITemplate template = templateService.getTemplateByName(templateName).get();
                if (template == null) {
                    Logger.log(LoggerType.WARNING, Logger.PREFIX + "The template » " + ConsoleColors.LIGHT_BLUE + templateName + ConsoleColors.GRAY + " doesn't exists!");
                } else {
                    int size = gameServerManager.getGameServersByTemplate(template).get().size();
                    if (size >= template.getMaxServerCount()) {
                        Logger.log(LoggerType.ERROR, Logger.PREFIX + "Cannot start the servers, the maximal server online count of » " + ConsoleColors.LIGHT_BLUE + template.getMaxServerCount()
                            + ConsoleColors.GRAY + " was reached! (Online » " + ConsoleColors.LIGHT_BLUE + size + ConsoleColors.GRAY + ")");
                        return;
                    }

                    Optional<IWrapper> optionalWrapperClient = wrapperManager.getWrappers().stream().findAny();

                    if (!optionalWrapperClient.isPresent()) {
                        Logger.log(LoggerType.ERROR, Logger.PREFIX + "No available Wrapper connected!");
                        return;
                    }

                    IWrapper wrapperClient = optionalWrapperClient.get();

                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Requesting start...");
                    SimpleGameServer newGameServer = new SimpleGameServer(wrapperClient, template.getName() + "-" + searchForAvailableID(template),
                        GameServerStatus.PENDING, null, snowflake.nextId(), template, System.currentTimeMillis(), template.getMotd(), template.getMaxPlayers(), false);
                    gameServerManager.registerGameServer(newGameServer);
                    wrapperClient.startServer(newGameServer);
                }
            } else if (args[0].equalsIgnoreCase("info")) {
                String name = args[1];
                IGameServer gameServer = gameServerManager.getGameServerByName(name).get();
                if (gameServer == null) {
                    Logger.log(LoggerType.WARNING, Logger.PREFIX + "The gameserver » " + ConsoleColors.LIGHT_BLUE + name + ConsoleColors.GRAY + " isn't online!");
                    return;
                }

                Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Information]----");
                Logger.newLine();
                Logger.log(LoggerType.INFO, Logger.PREFIX + "Gameserver » " + ConsoleColors.LIGHT_BLUE + gameServer.getName());
                Logger.newLine();
                Logger.log(LoggerType.INFO,
                    Logger.PREFIX + "Total memory » " + ConsoleColors.LIGHT_BLUE + gameServer.getTotalMemory() + "mb");
                Logger.newLine();
                Logger.log(LoggerType.INFO,
                    Logger.PREFIX + "Id » " + ConsoleColors.LIGHT_BLUE + "#" + gameServer.getSnowflake());
                Logger.newLine();
                Logger.log(LoggerType.INFO,
                    Logger.PREFIX + "Status » " + ConsoleColors.LIGHT_BLUE + gameServer.getStatus());
                Logger.newLine();
                Logger.log(LoggerType.INFO,
                    Logger.PREFIX + "Started time » " + ConsoleColors.LIGHT_BLUE + gameServer.getStartTime());
                Logger.newLine();
                Logger.log(LoggerType.INFO,
                    Logger.PREFIX + "Ping » " + ConsoleColors.LIGHT_BLUE + gameServer.getPing() + "ms");
                Logger.newLine();
                Logger.log(LoggerType.INFO, Logger.PREFIX + "----[/Information]----");

            } else if (args[0].equalsIgnoreCase("start") && args.length == 3) {
                String templateName = args[1];
                ITemplate template = templateService.getTemplateByName(templateName).get();
                if (template == null) {
                    Logger.log(LoggerType.WARNING, Logger.PREFIX + "The template » " + ConsoleColors.LIGHT_BLUE + templateName + ConsoleColors.GRAY + " doesn't exists!");
                } else {
                    String amountString = args[2];
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
                        Logger.log(LoggerType.ERROR, Logger.PREFIX + "Cannot start the server, the maximal server online count of » " + ConsoleColors.LIGHT_BLUE + template.getMaxServerCount()
                            + ConsoleColors.GRAY + " was reached! (With new servers » " + ConsoleColors.LIGHT_BLUE + (size + amount) + ConsoleColors.GRAY + ")");
                        return;
                    }

                    Optional<IWrapper> optionalWrapperClient = wrapperManager.getWrappers().stream().findAny();

                    if (!optionalWrapperClient.isPresent()) {
                        Logger.log(LoggerType.ERROR, Logger.PREFIX + "No available Wrapper connected!");
                        return;
                    }

                    IWrapper wrapperClient = optionalWrapperClient.get();

                    for (int i = 0; i < amount; i++) {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "Requesting start...");
                        SimpleGameServer newGameServer = new SimpleGameServer(wrapperClient, template.getName() + "-" + searchForAvailableID(template),
                            GameServerStatus.PENDING, null, snowflake.nextId(), template, System.currentTimeMillis(), template.getMotd(), template.getMaxPlayers(), false);
                        gameServerManager.registerGameServer(newGameServer);
                        wrapperClient.startServer(newGameServer);
                    }
                    Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN + "Successfully requested start for » " + ConsoleColors.LIGHT_BLUE + amount + ConsoleColors.GRAY + " servers!");
                }
            } else if (args[0].equalsIgnoreCase("copy")) {
                String name = args[1];
                String type = args[2];
                IGameServer gameServer = gameServerManager.getGameServerByName(name).get();
                if (gameServer == null) {
                    Logger.log(LoggerType.WARNING, Logger.PREFIX + "The gameserver » " + ConsoleColors.LIGHT_BLUE + name + ConsoleColors.GRAY + " isn't online!");
                    return;
                }
                if (!gameServer.getStatus().equals(GameServerStatus.RUNNING)) {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "The gameserver » " + ConsoleColors.LIGHT_BLUE + name + ConsoleColors.GRAY + " isn't completely stopped or started!");
                    return;
                }
                if (type.equalsIgnoreCase("worlds")) {
                    if (gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY)) {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "A Proxy Server doesn't have worlds, so its not compatibly with the 'worlds' mode, please use the 'entire' type!");
                        return;
                    }
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Copying " + ConsoleColors.LIGHT_BLUE + gameServer.getName() + ConsoleColors.GRAY + "...");
                    List<IWrapper> wrappers = wrapperManager.getWrappers().stream().filter(wrapperClient -> Arrays.asList(gameServer.getTemplate().getWrapperNames()).contains(wrapperClient.getName())).collect(Collectors.toList());
                    for (IWrapper wrapper : wrappers) {
                        wrapper.sendPacket(new APIRequestGameServerCopyPacket(APIRequestGameServerCopyPacket.Type.WORLD, gameServer.getName(), String.valueOf(gameServer.getSnowflake()), gameServer.getTemplate().getName()));
                    }
                } else if (type.equalsIgnoreCase("entire")) {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Copying " + ConsoleColors.LIGHT_BLUE + gameServer.getName() + ConsoleColors.GRAY + "...");
                    List<IWrapper> wrappers = wrapperManager.getWrappers().stream().filter(wrapperClient -> Arrays.asList(gameServer.getTemplate().getWrapperNames()).contains(wrapperClient.getName())).collect(Collectors.toList());
                    for (IWrapper wrapper : wrappers) {
                        wrapper.sendPacket(new APIRequestGameServerCopyPacket(APIRequestGameServerCopyPacket.Type.ENTIRE, gameServer.getName(), String.valueOf(gameServer.getSnowflake()), gameServer.getTemplate().getName()));
                    }
                } else {
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Use following command: " + ConsoleColors.LIGHT_BLUE +
                        "gameserver copy <gameserver> entire/worlds");
                    Logger.log(LoggerType.INFO, Logger.PREFIX + "Explanation: \n" +
                        Logger.PREFIX + ConsoleColors.LIGHT_BLUE + "entire: " + ConsoleColors.GRAY + "copies the entire GameServer to the template\n" +
                        Logger.PREFIX + ConsoleColors.LIGHT_BLUE + "worlds: " + ConsoleColors.GRAY + "only copies the worlds of the GameServer to the template");
                }
            } else if (args[0].equalsIgnoreCase("execute")) {
                String name = args[1];
                IGameServer gameServer = gameServerManager.getGameServerByName(name).get();
                if (gameServer == null) {
                    Logger.log(LoggerType.WARNING, Logger.PREFIX + "The gameserver » " + ConsoleColors.LIGHT_BLUE + name + ConsoleColors.GRAY + " isn't online!");
                    return;
                }

                String command = "";
                for (int i = 3; i < args.length; i++) {
                    command += args[i] + " ";
                }
                command = command.substring(0, command.length() - 1);
                Logger.log(LoggerType.INFO, Logger.PREFIX + "Processing...");
                gameServer.sendPacket(new GameServerExecuteCommandPacket(command));
                Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GREEN + "Successfully executed command » " + ConsoleColors.LIGHT_BLUE + command + ConsoleColors.GRAY + " on server » " + ConsoleColors.LIGHT_BLUE + gameServer.getName() + ConsoleColors.GRAY + "!");

            }
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
        }
    }

    private void sendHelp() {
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Gameserver]----");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "gameserver stop/shutdown <server> " + ConsoleColors.GRAY + "to shutdown a gameserver");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "gameserver start <template> " + ConsoleColors.GRAY + "to start a new gameserver");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "gameserver start <server> <amount> " + ConsoleColors.GRAY + "to start multiple new gameservers at once");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "gameserver copy <server> worlds/entire " + ConsoleColors.GRAY + "to copy the temp files of a server into its template");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "gameserver info <server> " + ConsoleColors.GRAY + "to get information of a gameserver");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "gameserver execute <server> <command> " + ConsoleColors.GRAY + "to execute a command on a gameserver");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[/Gameserver]----");
    }

    private int searchForAvailableID(ITemplate template) throws ExecutionException, InterruptedException {
        return gameServerManager.getGameServersByTemplate(template).get().size() + 1;
    }
}
