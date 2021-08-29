package de.polocloud.bootstrap.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.*;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.command.identifier.TabCompletable;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.module.CloudModule;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.module.ModuleLocalCache;
import de.polocloud.logger.log.types.ConsoleColors;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ReloadCommand implements CommandListener, TabCompletable {

    @Command(name = "reload", description = "Reloads a module or the entire cloud", aliases = "rl")
    @CommandPermission("command.reload")
    @CommandExecutors(ExecutorType.CONSOLE)
    public void execute(CommandExecutor sender, String[] fullArgs, @Arguments(onlyFirstArgs = {"debug", "all", "module"}, min = 1, max = 2, message = {"----[Reload]----", "Use §3reload all §7to reload the clouds", "Use §3reload module <module> §7to reload a module", "----[Reload]----"}) String... args) {

        if (args[0].equalsIgnoreCase("all")) {
            long start = System.currentTimeMillis();
            PoloLogger.print(LogLevel.INFO, "Initializing cloud reload...");
            Master.getInstance().getModuleCache().unloadModules();
            Master.getInstance().getModuleLoader().loadModules(true);
            PoloCloudAPI.getInstance().updateCache();

            PoloLogger.print(LogLevel.INFO, "Cloud " + ConsoleColors.GREEN + "completed " + ConsoleColors.GRAY + "reload. (" + (System.currentTimeMillis() - start) + "ms)");

        } else if (args[0].equalsIgnoreCase("module") && args.length == 2) {
            String moduleName = args[1];
            CloudModule module = Master.getInstance().getModuleCache().getModuleByName(moduleName);
            if (module == null) {
                PoloLogger.print(LogLevel.WARNING, "The module » " + ConsoleColors.LIGHT_BLUE + moduleName + ConsoleColors.GRAY + " isn't loaded!");
                return;
            }
            long started = System.currentTimeMillis();
            PoloLogger.print(LogLevel.INFO, "Reloading » " + ConsoleColors.LIGHT_BLUE + Master.getInstance().getModuleCache().get(module).getModuleData().getName() + ConsoleColors.GRAY + "...");
            File moduleFile = Master.getInstance().getModuleCache().get(module).getModuleData().getFile();
            Master.getInstance().getModuleCache().unloadModule(module);
            Master.getInstance().getModuleLoader().loadModule(moduleFile);
            PoloLogger.print(LogLevel.INFO, "Reload completed! (took " + (System.currentTimeMillis() - started) + "ms)");

        }
    }

    @Override
    public List<String> onTabComplete(CommandExecutor executor, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("module")) {
            List<String> data = new LinkedList<>();
            for (ModuleLocalCache value : Master.getInstance().getModuleCache().values()) {
                data.add(value.getModuleData().getName());
            }
            return data;
        }
        return Arrays.asList("all", "module");
    }
}
