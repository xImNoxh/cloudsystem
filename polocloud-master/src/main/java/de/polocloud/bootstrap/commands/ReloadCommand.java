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
import de.polocloud.api.module.info.ModuleState;
import de.polocloud.api.module.loader.ModuleService;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.IPropertyManager;
import de.polocloud.bootstrap.Master;
import de.polocloud.logger.log.types.ConsoleColors;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class ReloadCommand implements CommandListener, TabCompletable {

    @Command(name = "reload", description = "Reloads a module or the entire cloud", aliases = "rl")
    @CommandPermission("command.reload")
    @CommandExecutors(ExecutorType.CONSOLE)
    public void execute(CommandExecutor sender, String[] fullArgs, @Arguments(onlyFirstArgs = {"debug", "all", "module"}, min = 1, max = 2, message = {"----[Reload]----", "Use §3reload all §7to reload the clouds", "Use §3reload module <module> §7to reload a module", "----[Reload]----"}) String... args) {

        if (args[0].equalsIgnoreCase("all")) {
            long start = System.currentTimeMillis();
            PoloLogger.print(LogLevel.INFO, "Initializing cloud reload...");
            PoloCloudAPI.getInstance().reload();
            PoloLogger.print(LogLevel.INFO, "Cloud " + ConsoleColors.GREEN + "completed " + ConsoleColors.GRAY + "reload. (" + (System.currentTimeMillis() - start) + "ms)");

        } else if (args[0].equalsIgnoreCase("module") && args.length == 2) {
            String moduleName = args[1];
            ModuleService moduleService = Master.getInstance().getModuleService();
            CloudModule module = moduleService.getModule(moduleName);

            if (module == null) {
                PoloLogger.print(LogLevel.WARNING, "§cThe module §e" + moduleName + " §cseems not to be loaded!");
                return;
            }
            long started = System.currentTimeMillis();
            PoloLogger.print(LogLevel.INFO, "§7Reloading Module §b" + module.info().name() + "§7...");
            moduleService.callTasks(module, ModuleState.RELOADING);
            PoloLogger.print(LogLevel.INFO, "Reload completed! (took " + (System.currentTimeMillis() - started) + "ms)");

        }
    }

    @Override
    public List<String> onTabComplete(CommandExecutor executor, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("module")) {
            List<String> data = new LinkedList<>();
            for (CloudModule value : Master.getInstance().getModuleService().getModules()) {
                data.add(value.info().name());
            }
            return data;
        }
        return Arrays.asList("all", "module");
    }
}
