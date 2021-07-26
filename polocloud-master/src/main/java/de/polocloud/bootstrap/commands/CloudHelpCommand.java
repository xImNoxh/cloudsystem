package de.polocloud.bootstrap.commands;

import de.polocloud.api.CloudAPI;
import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.util.stream.Collectors;

@CloudCommand.Info(name = "help", description = "", aliases = "", commandType = CommandType.CONSOLE)
public class CloudHelpCommand extends CloudCommand {

    @Override
    public void execute(ICommandExecutor commandSender, String[] args) {
        CloudAPI.getInstance().getCommandPool().getAllCachedCommands().stream().filter(it ->
            it.getCommandType().equals(CommandType.CONSOLE) || it.getCommandType().equals(CommandType.INGAME_CONSOLE)).forEach(key ->

            Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.LIGHT_BLUE.getAnsiCode() + key.getName() +
                ConsoleColors.GRAY.getAnsiCode() + " × " + ConsoleColors.GRAY.getAnsiCode() + key.getDescription()));

        int ingame = CloudAPI.getInstance().getCommandPool().getAllCachedCommands().stream().filter(it ->
            it.getCommandType().equals(CommandType.CONSOLE)).collect(Collectors.toList()).size();

        int console = CloudAPI.getInstance().getCommandPool().getAllCachedCommands().stream().filter(it ->
            it.getCommandType().equals(CommandType.INGAME)).collect(Collectors.toList()).size();

        int both = CloudAPI.getInstance().getCommandPool().getAllCachedCommands().stream().filter(it ->
            it.getCommandType().equals(CommandType.INGAME)).collect(Collectors.toList()).size();

        Logger.log(LoggerType.INFO, Logger.PREFIX + ConsoleColors.GRAY.getAnsiCode() + "Founded " +
            ConsoleColors.LIGHT_BLUE.getAnsiCode() +  CloudAPI.getInstance().getCommandPool().getAllCachedCommands().size()
            + ConsoleColors.GRAY.getAnsiCode() +  " commands in the cloud. (" + ingame + " InGame Commands / " + console + " Console Commands / " + both + " both)");
    }

}
