package de.polocloud.bootstrap.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Arguments;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.command.identifier.TabCompletable;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.packets.gameserver.GameServerExecuteCommandPacket;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.api.logger.helper.LogLevel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class GameServerCommand implements CommandListener, TabCompletable {

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
        IWrapperManager wrapperManager = PoloCloudAPI.getInstance().getWrapperManager();
        IGameServerManager gameServerManager = PoloCloudAPI.getInstance().getGameServerManager();
        ITemplateManager templateManager = PoloCloudAPI.getInstance().getTemplateManager();
        if (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("shutdown")) {
            String name = args[1];
            IGameServer gameServer = gameServerManager.getCached(name);

            if (gameServer == null) {
                PoloLogger.print(LogLevel.WARNING, "The gameserver » " + ConsoleColors.LIGHT_BLUE + name + ConsoleColors.GRAY + " isn't online!");
            } else {
                if (gameServer.getStatus() == GameServerStatus.AVAILABLE) {
                    gameServer.terminate();
                    PoloLogger.print(LogLevel.INFO, "Requesting §7'§b" + gameServer.getWrapper().getName() + "§7' to stop §3" + gameServer.getName() + "§7#§b" + gameServer.getSnowflake() + "§7..." );
                } else {
                    gameServer.terminate();
                    PoloLogger.print(LogLevel.WARNING, "The GameServer §e" + gameServer.getName() + " §7is not Running... §cTerminating Process§7...");
                }
            }
        } else if (args[0].equalsIgnoreCase("start") && args.length == 2) {
            String templateName = args[1];
            ITemplate template = templateManager.getTemplate(templateName);
            if (template == null) {
                PoloLogger.print(LogLevel.WARNING, "The template » " + ConsoleColors.LIGHT_BLUE + templateName + ConsoleColors.GRAY + " doesn't exists!");
            } else {
                int size = gameServerManager.getAllCached(template).size();
                if (size >= template.getMaxServerCount()) {
                    PoloLogger.print(LogLevel.ERROR, "Cannot start the servers, the maximal server online count of » " + ConsoleColors.LIGHT_BLUE + template.getMaxServerCount()
                        + ConsoleColors.GRAY + " was reached! (Online » " + ConsoleColors.LIGHT_BLUE + size + ConsoleColors.GRAY + ")");
                    return;
                }

                Optional<IWrapper> optionalWrapperClient = wrapperManager.getWrappers().stream().findAny();

                if (!optionalWrapperClient.isPresent()) {
                    PoloLogger.print(LogLevel.ERROR, "No available Wrapper connected!");
                    return;
                }

                IWrapper wrapperClient = optionalWrapperClient.get();
                int id = gameServerManager.getFreeId(template);

                PoloLogger.print(LogLevel.INFO, "Requesting start...");
                SimpleGameServer gameServer = new SimpleGameServer(id, template.getMotd(), GameServerStatus.STARTING, Snowflake.getInstance().nextId(), System.currentTimeMillis(), template.getMaxMemory(), PoloCloudAPI.getInstance().getGameServerManager().getFreePort(template), template.getMaxPlayers(), template.getName());
                wrapperClient.startServer(gameServer);
            }
        } else if (args[0].equalsIgnoreCase("info")) {
            String name = args[1];
            IGameServer gameServer = gameServerManager.getCached(name);
            if (gameServer == null) {
                PoloLogger.print(LogLevel.WARNING, "The gameserver » " + ConsoleColors.LIGHT_BLUE + name + ConsoleColors.GRAY + " isn't online!");
                return;
            }

            PoloLogger.print(LogLevel.INFO, "----[Information]----");
            PoloLogger.print(LogLevel.INFO, "Gameserver » " + ConsoleColors.LIGHT_BLUE + gameServer.getName());
            PoloLogger.print(LogLevel.INFO, "Total memory » " + ConsoleColors.LIGHT_BLUE + gameServer.getTotalMemory() + "mb");
            PoloLogger.print(LogLevel.INFO, "Id » " + ConsoleColors.LIGHT_BLUE + "#" + gameServer.getSnowflake());
            PoloLogger.print(LogLevel.INFO, "Status » " + ConsoleColors.LIGHT_BLUE + gameServer.getStatus());
            PoloLogger.print(LogLevel.INFO, "Started time » " + ConsoleColors.LIGHT_BLUE + gameServer.getStartTime());
            PoloLogger.print(LogLevel.INFO, "----[/Information]----");

        } else if (args[0].equalsIgnoreCase("start") && args.length == 3) {
            String templateName = args[1];
            ITemplate template = templateManager.getTemplate(templateName);
            if (template == null) {
                PoloLogger.print(LogLevel.WARNING, "The template » " + ConsoleColors.LIGHT_BLUE + templateName + ConsoleColors.GRAY + " doesn't exists!");
            } else {
                String amountString = args[2];
                int amount;
                try {
                    amount = Integer.parseInt(amountString);
                } catch (NumberFormatException exception) {
                    PoloLogger.print(LogLevel.ERROR, "Please provide a real number (int)");
                    return;
                }

                if (amount < 0) {
                    PoloLogger.print(LogLevel.ERROR, "You cannot start " + amountString + " servers!");
                    return;
                }
                int size = gameServerManager.getAllCached(template).size();
                if ((size + amount) >= template.getMaxServerCount()) {
                    PoloLogger.print(LogLevel.ERROR, "Cannot start the server, the maximal server online count of » " + ConsoleColors.LIGHT_BLUE + template.getMaxServerCount()
                        + ConsoleColors.GRAY + " was reached! (With new servers » " + ConsoleColors.LIGHT_BLUE + (size + amount) + ConsoleColors.GRAY + ")");
                    return;
                }

                Optional<IWrapper> optionalWrapperClient = wrapperManager.getWrappers().stream().findAny();

                if (!optionalWrapperClient.isPresent()) {
                    PoloLogger.print(LogLevel.ERROR, "No available Wrapper connected!");
                    return;
                }

                IWrapper wrapperClient = optionalWrapperClient.get();
                int id = gameServerManager.getFreeId(template);

                for (int i = 0; i < amount; i++) {
                    PoloLogger.print(LogLevel.INFO, "Requesting start...");

                    SimpleGameServer gameServer = new SimpleGameServer(id, template.getMotd(), GameServerStatus.STARTING, Snowflake.getInstance().nextId(), System.currentTimeMillis(), template.getMaxMemory(), PoloCloudAPI.getInstance().getGameServerManager().getFreePort(template), template.getMaxPlayers(), template.getName());
                    wrapperClient.startServer(gameServer);
                }
                PoloLogger.print(LogLevel.INFO, ConsoleColors.GREEN + "Successfully requested start for » " + ConsoleColors.LIGHT_BLUE + amount + ConsoleColors.GRAY + " servers!");
            }
        } else if (args[0].equalsIgnoreCase("copy")) {
            String name = args[1];
            String type = args[2];
            IGameServer gameServer = gameServerManager.getCached(name);
            if (gameServer == null) {
                PoloLogger.print(LogLevel.WARNING, "The gameserver » " + ConsoleColors.LIGHT_BLUE + name + ConsoleColors.GRAY + " isn't online!");
                return;
            }
            if (!gameServer.getStatus().equals(GameServerStatus.AVAILABLE)) {
                PoloLogger.print(LogLevel.INFO, "The gameserver » " + ConsoleColors.LIGHT_BLUE + name + ConsoleColors.GRAY + " isn't completely stopped or started!");
                return;
            }
            if (type.equalsIgnoreCase("worlds")) {
                if (gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY)) {
                    PoloLogger.print(LogLevel.INFO, "A Proxy Server doesn't have worlds, so its not compatibly with the 'worlds' mode, please use the 'entire' type!");
                    return;
                }
                PoloLogger.print(LogLevel.INFO, "Copying " + ConsoleColors.LIGHT_BLUE + gameServer.getName() + ConsoleColors.GRAY + "...");
                templateManager.copyServer(gameServer, ITemplateManager.Type.WORLD);

            } else if (type.equalsIgnoreCase("entire")) {
                PoloLogger.print(LogLevel.INFO, "Copying " + ConsoleColors.LIGHT_BLUE + gameServer.getName() + ConsoleColors.GRAY + "...");
                templateManager.copyServer(gameServer, ITemplateManager.Type.ENTIRE);

            } else {
                PoloLogger.print(LogLevel.INFO, "Use following command: " + ConsoleColors.LIGHT_BLUE +
                    "gameserver copy <gameserver> entire/worlds");
                PoloLogger.print(LogLevel.INFO, "Explanation: \n" +
                    ConsoleColors.LIGHT_BLUE + "entire: " + ConsoleColors.GRAY + "copies the entire GameServer to the template\n" +
                    ConsoleColors.LIGHT_BLUE + "worlds: " + ConsoleColors.GRAY + "only copies the worlds of the GameServer to the template");
            }
        } else if (args[0].equalsIgnoreCase("execute")) {
            String name = args[1];
            IGameServer gameServer = gameServerManager.getCached(name);
            if (gameServer == null) {
                PoloLogger.print(LogLevel.WARNING, "The gameserver » " + ConsoleColors.LIGHT_BLUE + name + ConsoleColors.GRAY + " isn't online!");
                return;
            }

            StringBuilder command = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                command.append(args[i]).append(" ");
            }
            command = new StringBuilder(command.substring(0, command.length() - 1));
            PoloLogger.print(LogLevel.INFO, "Processing...");
            gameServer.sendPacket(new GameServerExecuteCommandPacket(command.toString(), gameServer.getName()));
            PoloLogger.print(LogLevel.INFO, ConsoleColors.GREEN + "Successfully executed command » " + ConsoleColors.LIGHT_BLUE + command + ConsoleColors.GRAY + " on server » " + ConsoleColors.LIGHT_BLUE + gameServer.getName() + ConsoleColors.GRAY + "!");

        }
    }

    private void sendHelp() {
        PoloLogger.print(LogLevel.INFO, "----[Gameserver]----");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "gameserver stop/shutdown <server> " + ConsoleColors.GRAY + "to shutdown a gameserver");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "gameserver start <template> " + ConsoleColors.GRAY + "to start a new gameserver");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "gameserver start <server> <amount> " + ConsoleColors.GRAY + "to start multiple new gameservers at once");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "gameserver copy <server> worlds/entire " + ConsoleColors.GRAY + "to copy the temp files of a server into its template");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "gameserver info <server> " + ConsoleColors.GRAY + "to get information of a gameserver");
        PoloLogger.print(LogLevel.INFO, "Use " + ConsoleColors.LIGHT_BLUE + "gameserver execute <server> <command> " + ConsoleColors.GRAY + "to execute a command on a gameserver");
        PoloLogger.print(LogLevel.INFO, "----[/Gameserver]----");
    }

    @Override
    public List<String> onTabComplete(CommandExecutor executor, String[] args) {

        if(args.length == 0){
            return Arrays.asList("stop", "start", "copy", "info", "execute");
        }else if(args.length == 1){
            LinkedList<String> strings = new LinkedList<>();
            for (IGameServer gameServer : PoloCloudAPI.getInstance().getGameServerManager().getAllCached()) {
                strings.add(gameServer.getName());
            }
            return strings;
        }else if(args.length == 2){
            if(args[0].equalsIgnoreCase("copy")){
                return Arrays.asList("worlds", "entire");
            }
        }
        return new LinkedList<>();
    }
}
