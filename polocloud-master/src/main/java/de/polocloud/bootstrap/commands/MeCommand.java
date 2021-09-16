package de.polocloud.bootstrap.commands;

import de.polocloud.api.APIVersion;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.network.protocol.packet.PacketFactory;
import de.polocloud.bootstrap.Master;
import de.polocloud.client.PoloCloudUpdater;
import de.polocloud.api.console.ConsoleColors;

import java.util.Arrays;

public class MeCommand implements CommandListener {

    @Command(name = "me", description = "Information about the Cloud", aliases = "cloudinfo")
    public void execute(CommandExecutor sender, String[] fullArgs, String... params) {
        APIVersion version = PoloCloudAPI.getInstance().getVersion();

        PoloLogger.print(LogLevel.INFO, "\n" +
            "  _____      _        _____ _                 _ \n" +
            " |  __ \\    | |      / ____| |               | |\n" +
            " | |__) |__ | | ___ | |    | | ___  _   _  __| |\n" +
            " |  ___/ _ \\| |/ _ \\| |    | |/ _ \\| | | |/ _` |\n" +
            " | |  | (_) | | (_) | |____| | (_) | |_| | (_| |\n" +
            " |_|   \\___/|_|\\___/ \\_____|_|\\___/ \\__,_|\\__,_|\n" +
            "                                                \n");
        PoloLogger.print(LogLevel.INFO, "#This cloud was developed by " + ConsoleColors.LIGHT_BLUE + Arrays.toString(version.developers()).replace("[", "").replace("]", ""));
        PoloLogger.print(LogLevel.INFO, ConsoleColors.GRAY + "#Version of cloud - " + ConsoleColors.LIGHT_BLUE + version.version() + ConsoleColors.GRAY + " (" + version.identifier() + ") | ©opyright by PoloCloud.");

        PoloLogger.newLine();
        PoloLogger.print(LogLevel.INFO, "§7Connected wrappers » §3" + PoloCloudAPI.getInstance().getWrapperManager().getWrappers().size());
        PoloLogger.print(LogLevel.INFO, "§7Loaded commands » §3" + PoloCloudAPI.getInstance().getCommandManager().getCommands().size());
        PoloLogger.print(LogLevel.INFO, "§7Loaded templates » §3" + PoloCloudAPI.getInstance().getTemplateManager().getTemplates().size());
        PoloLogger.print(LogLevel.INFO, "§7Loaded modules » §3" + Master.getInstance().getModuleService().getModules().size());
        PoloLogger.print(LogLevel.INFO, "§7Registered packets » §3" + PacketFactory.REGISTERED_PACKETS.size());
        PoloLogger.print(LogLevel.INFO, "§7Registered fallbacks » §3" + PoloCloudAPI.getInstance().getFallbackManager().getAvailableFallbacks().size());
        PoloLogger.print(LogLevel.INFO, "§7Online players » §3" + PoloCloudAPI.getInstance().getCloudPlayerManager().getAllCached().size());
        PoloLogger.print(LogLevel.INFO, "§7Running gameservers » §3" + PoloCloudAPI.getInstance().getGameServerManager().getAllCached().size());
        PoloLogger.print(LogLevel.INFO, "§7Running threads » §3" + Thread.activeCount());

        PoloLogger.newLine();
        PoloLogger.print(LogLevel.INFO, "§7System CPU load » §3" + PoloCloudAPI.getInstance().getSystemManager().getResourceConverter().roundDouble(PoloCloudAPI.getInstance().getSystemManager().getResourceProvider().getProcessCpuLoad()) + "%");
        PoloLogger.print(LogLevel.INFO, "§7Process CPU Load » §3" + PoloCloudAPI.getInstance().getSystemManager().getResourceConverter().roundDouble(PoloCloudAPI.getInstance().getSystemManager().getResourceProvider().getProcessCpuLoad()) + "%");
        PoloLogger.print(LogLevel.INFO, "§7System memory » §3" + PoloCloudAPI.getInstance().getSystemManager().getResourceConverter().convertLongToSize(PoloCloudAPI.getInstance().getSystemManager().getResourceProvider().getSystemPhysicalMemory()));
        PoloLogger.print(LogLevel.INFO, "§7Used memory » §3" + PoloCloudAPI.getInstance().getSystemManager().getResourceConverter().convertLongToSize(PoloCloudAPI.getInstance().getSystemManager().getResourceProvider().getSystemUsedMemory()));
        PoloLogger.print(LogLevel.INFO, "§7Available memory » §3" + PoloCloudAPI.getInstance().getSystemManager().getResourceConverter().convertLongToSize(PoloCloudAPI.getInstance().getSystemManager().getResourceProvider().getSystemFreeMemory()));
        PoloLogger.newLine();

        PoloLogger.print(LogLevel.INFO, "§7Checking your version...");
        new Thread(() -> {
            PoloCloudUpdater cloudUpdater = new PoloCloudUpdater(false, Master.getInstance().getVersion().version(), "bootstrap", null);
            boolean notUpToDate = cloudUpdater.check();
            if (notUpToDate) {
                PoloLogger.print(LogLevel.INFO, "§7You are not running the latest version! (you: " + Master.getInstance().getVersion().version() + " newest:" + cloudUpdater.getFetchedVersion() + " (date: " + cloudUpdater.getLastUpdate() + "))");
            } else {
                PoloLogger.print(LogLevel.INFO, "§7You are running the latest version! (" + Master.getInstance().getVersion().version() + ")");
            }
        }).start();

    }

}
