package de.polocloud.wrapper.impl.commands;


import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ConsoleExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.command.identifier.TabCompletable;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.wrapper.Wrapper;
import de.polocloud.wrapper.manager.screen.IScreen;
import de.polocloud.wrapper.manager.screen.IScreenManager;

import java.util.LinkedList;
import java.util.List;

public class ScreenCommand implements CommandListener, TabCompletable {

    @Command(
        name = "screen",
        aliases = {"screens"},
        usage = "screen <server | leave>",
        description = "Manages all Server outputs!"
    )
    public void execute(CommandExecutor sender, String[] fullArgs, String[] args) {
        IScreenManager screenManager = Wrapper.getInstance().getScreenManager();
        if (args.length == 1) {
            String subject = args[0];
            if (subject.equalsIgnoreCase("leave")) {
                if (screenManager.isInScreen()) {
                    screenManager.quitCurrentScreen();
                    screenManager.quitCurrentScreen();
                } else {
                    PoloLogger.print(LogLevel.ERROR, "§cYou are not in a screen Session!");
                }
            } else {
                String serverName = args[0];
                IScreen screen = screenManager.getScreen(serverName.toLowerCase());
                if (screen != null && screen.getCachedLines() != null) {
                    if (screen.getCachedLines().isEmpty()) {
                        PoloLogger.print(LogLevel.WARNING, "§7This screen doesn't have anything to §edisplay §7yet!");
                        return;
                    }

                    screen.setPrinter((ConsoleExecutor) Wrapper.getInstance().getCommandExecutor());
                    PoloCloudAPI.getInstance().getCommandManager().setFilter(iCommandRunner -> iCommandRunner.getListener().equals(ScreenCommand.this));
                    PoloLogger.print(LogLevel.INFO, "§7You joined screen §2" + serverName + " §7!");
                    screenManager.prepare(screen);
                    for (String cachedLine : new LinkedList<>(screen.getCachedLines())) {
                        PoloLogger.print(LogLevel.INFO, screen.formatLine(cachedLine));
                    }
                } else {
                    PoloLogger.print(LogLevel.WARNING, "§7The server §e" + serverName + " §7seems not to be online!");
                }
            }
        } else {
            this.sendUsage(sender);
        }

    }

    private void sendUsage(CommandExecutor sender) {
        PoloLogger.print(LogLevel.INFO, "§9screen <server> §7| §bJoins screen session");
        PoloLogger.print(LogLevel.INFO, "§9screen <leave> §7| §bLeaves screen session");
    }

    @Override
    public List<String> onTabComplete(CommandExecutor executor, String[] args) {
        List<String> list = new LinkedList<>();
        for (IGameServer IService : PoloCloudAPI.getInstance().getGameServerManager().getAllCached()) {
            if (PoloCloudAPI.getInstance().getGameServerManager().getCached(IService.getName()) == null) {
                continue;
            }
            list.add(IService.getName());
        }
        return list;
    }

}
