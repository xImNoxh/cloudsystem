package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.module.Module;
import de.polocloud.bootstrap.Master;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.io.File;

@CloudCommand.Info(name = "reload", description = "Reloads a module or the entire cloud", aliases = "rl", commandType = CommandType.CONSOLE)
public class ReloadCommand extends CloudCommand {

    @Override
    public void execute(ICommandExecutor sender, String[] args) {
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("all")) {
                long start = System.currentTimeMillis();
                Logger.log(LoggerType.INFO, Logger.PREFIX + "Initializing cloud reload...");
                Master.getInstance().getModuleCache().unloadModules();
                Master.getInstance().getModuleLoader().loadModules(true);

                Logger.log(LoggerType.INFO, Logger.PREFIX + "Cloud " + ConsoleColors.GREEN.getAnsiCode() + "completed "
                    + ConsoleColors.GRAY.getAnsiCode() + "reload. (" + (System.currentTimeMillis() - start) + "ms)");
            } else {
                sendHelp();
            }
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("module")) {
                String moduleName = args[2];
                Module module = Master.getInstance().getModuleCache().getModuleByName(moduleName);
                if (module == null) {
                    Logger.log(LoggerType.WARNING, Logger.PREFIX + "The module » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + moduleName + ConsoleColors.GRAY.getAnsiCode() + " isn't loaded!");
                    return;
                }
                long started = System.currentTimeMillis();
                Logger.log(LoggerType.INFO, Logger.PREFIX + "Reloading » " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + Master.getInstance().getModuleCache().get(module).getModuleData().getName() + ConsoleColors.GRAY.getAnsiCode() + "...");
                File moduleFile = Master.getInstance().getModuleCache().get(module).getModuleData().getFile();
                Master.getInstance().getModuleCache().unloadModule(module);
                Master.getInstance().getModuleLoader().loadModule(moduleFile);
                Logger.log(LoggerType.INFO, Logger.PREFIX + "Reload completed! (took " + (System.currentTimeMillis() - started) + "ms)");
            } else {
                sendHelp();
            }
        } else {
            sendHelp();
        }
    }

    private void sendHelp() {
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[Reload]----");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "reload all " + ConsoleColors.GRAY.getAnsiCode() + "to reload the clouds");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE.getAnsiCode() + "reload module <module> " + ConsoleColors.GRAY.getAnsiCode() + "to reload a module");
        Logger.newLine();
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[/Reload]----");
    }
}
