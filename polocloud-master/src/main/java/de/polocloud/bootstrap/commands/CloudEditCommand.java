package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.config.loader.IConfigLoader;
import de.polocloud.api.config.loader.SimpleConfigLoader;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerMaintenanceUpdatePacket;
import de.polocloud.api.network.protocol.packet.gameserver.GameServerMaxPlayersUpdatePacket;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.ITemplateService;
import de.polocloud.api.template.TemplateType;
import de.polocloud.bootstrap.config.MasterConfig;
import de.polocloud.bootstrap.config.messages.Messages;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

@CloudCommand.Info(name = "edit", description = "edit templates", aliases = "", commandType = CommandType.CONSOLE)
public class CloudEditCommand extends CloudCommand {

    private ITemplateService templateService;
    private IGameServerManager gameServerManager;

    private MasterConfig masterConfig;

    public CloudEditCommand(ITemplateService templateService, IGameServerManager gameServerManager) {
        this.templateService = templateService;
        this.gameServerManager = gameServerManager;

        File configFile = new File("config.json");
        IConfigLoader configLoader = new SimpleConfigLoader();
        masterConfig = configLoader.load(MasterConfig.class, configFile);
    }

    @Override
    public void execute(ICommandExecutor commandSender, String[] args) {

        if (args.length == 6) {

            if (args[1].equalsIgnoreCase("template")) {
                try {
                    ITemplate template = null;
                    template = templateService.getTemplateByName(args[2]).get();


                    if (template == null) {
                        Logger.log(LoggerType.INFO, Logger.PREFIX + "This template is not exists.");
                        return;
                    }

                    if (args[3].equalsIgnoreCase("set")) {
                        Messages messages = masterConfig.getMessages();
                        if (args[4].equalsIgnoreCase("maintenance")) {

                            if (!(args[5].equalsIgnoreCase("true") || args[5].equalsIgnoreCase("false"))) {
                                Logger.log(LoggerType.INFO, Logger.PREFIX + "Use following command: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() +
                                    "edit template <template> set maintenance <true/false>");
                                return;
                            }

                            Boolean state = Boolean.parseBoolean(args[5]);

                            template.setMaintenance(state);
                            templateService.getTemplateSaver().save(template);

                            try {
                                for (IGameServer gameServer : gameServerManager.getGameServersByTemplate(template).get()) {
                                    gameServer.sendPacket(new GameServerMaintenanceUpdatePacket(template.isMaintenance(),
                                        gameServer.getTemplate().getTemplateType() == TemplateType.PROXY ?
                                            messages.getProxyMaintenanceMessage() : messages.getGroupMaintenanceMessage()));
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }

                            Logger.log(LoggerType.INFO, Logger.PREFIX + "You update the " + ConsoleColors.CYAN.getAnsiCode() + "maintenance " + ConsoleColors.GRAY.getAnsiCode() + "state for template "
                                + ConsoleColors.LIGHT_BLUE.getAnsiCode() + template.getName());
                            return;
                        }

                        if (args[4].equalsIgnoreCase("maxplayers")) {

                            if (!isInteger().test(args[5])) {
                                Logger.log(LoggerType.INFO, Logger.PREFIX + "The last argument must be an integer.");
                                return;
                            }
                            template.setMaxPlayers(Integer.parseInt(args[5]));
                            templateService.getTemplateSaver().save(template);

                            try {
                                for (IGameServer gameServer : gameServerManager.getGameServersByTemplate(template).get()) {
                                    gameServer.sendPacket(new GameServerMaxPlayersUpdatePacket(gameServer.getTemplate().getTemplateType().equals(TemplateType.PROXY) ? messages.getNetworkIsFull() : messages.getServiceIsFull(), gameServer.getMaxPlayers()));
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            Logger.log(LoggerType.INFO, Logger.PREFIX + "You update the " + ConsoleColors.CYAN.getAnsiCode() + "max players amount " + ConsoleColors.GRAY.getAnsiCode() + "for template "
                                + ConsoleColors.LIGHT_BLUE.getAnsiCode() + template.getName());
                            return;

                        }


                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use following command: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() +
            "edit template <template> set maintenance <true/false>");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use following command: " + ConsoleColors.LIGHT_BLUE.getAnsiCode() +
            "edit template <template> set maxplayers <value>");
    }

    public Predicate<String> isInteger() {
        return s -> {
            try {
                Integer.parseInt(String.valueOf(s));
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        };
    }

}
