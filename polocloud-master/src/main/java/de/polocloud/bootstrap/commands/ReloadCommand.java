package de.polocloud.bootstrap.commands;

import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.annotation.CommandExecutors;
import de.polocloud.api.command.annotation.CommandPermission;
import de.polocloud.api.command.annotation.MaxArgs;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.module.CloudModule;
import de.polocloud.bootstrap.Master;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.ConsoleColors;
import de.polocloud.logger.log.types.LoggerType;

import java.io.File;

public class ReloadCommand implements CommandListener {

    @Command(name = "reload", description = "Reloads a module or the entire cloud", aliases = "rl")
    @CommandPermission("command.reload")
    @CommandExecutors(ExecutorType.CONSOLE)
    public void execute(CommandExecutor sender, String[] args) {
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("all")) {
                long start = System.currentTimeMillis();
                Logger.log(LoggerType.INFO, Logger.PREFIX + "Initializing cloud reload...");
                Master.getInstance().getModuleCache().unloadModules();
                Master.getInstance().getModuleLoader().loadModules(true);

                Logger.log(LoggerType.INFO, Logger.PREFIX + "Cloud " + ConsoleColors.GREEN + "completed "
                    + ConsoleColors.GRAY + "reload. (" + (System.currentTimeMillis() - start) + "ms)");
            } else {
                sendHelp();
            }
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("module")) {
                String moduleName = args[2];
                CloudModule module = Master.getInstance().getModuleCache().getModuleByName(moduleName);
                if (module == null) {
                    Logger.log(LoggerType.WARNING, Logger.PREFIX + "The module » " + ConsoleColors.LIGHT_BLUE + moduleName + ConsoleColors.GRAY + " isn't loaded!");
                    return;
                }
                long started = System.currentTimeMillis();
                Logger.log(LoggerType.INFO, Logger.PREFIX + "Reloading » " + ConsoleColors.LIGHT_BLUE + Master.getInstance().getModuleCache().get(module).getModuleData().getName() + ConsoleColors.GRAY + "...");
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
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "reload all " + ConsoleColors.GRAY + "to reload the clouds");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "Use " + ConsoleColors.LIGHT_BLUE + "reload module <module> " + ConsoleColors.GRAY + "to reload a module");
        Logger.log(LoggerType.INFO, Logger.PREFIX + "----[/Reload]----");
    }
}
