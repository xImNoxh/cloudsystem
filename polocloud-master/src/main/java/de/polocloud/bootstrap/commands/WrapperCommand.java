package de.polocloud.bootstrap.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Arguments;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.command.identifier.TabCompletable;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class WrapperCommand implements CommandListener, TabCompletable {

    public WrapperCommand() {
    }


    @Command(name = "wrapper", description = "Manage a wrapper", aliases = "wrap")
    public void execute(CommandExecutor sender, String[] fullArgs, @Arguments(onlyFirstArgs = {"stop", "shutdown", "info", "list"}, min = 1, max = 2, message = {"----[Wrapper]----", "Use §3wrapper stop/shutdown <wrapper> §7to shutdown a wrapper", "Use §3wrapper list §7to get all connected wrappers", "Use §3wrapper info <wrapper> §7to get information of a wrapper", "----[Wrapper]----"}) String... params) {
        IWrapperManager wrapperManager = PoloCloudAPI.getInstance().getWrapperManager();
        if (params.length == 1 && params[0].equalsIgnoreCase("list")) {
            if (wrapperManager.getWrappers().isEmpty()) {
                sender.sendMessage("§cUnfortunately there are no connected Wrappers at the moment!");
                return;
            }
            sender.sendMessage("----[Wrappers]----");
            for (IWrapper wrapper : wrapperManager.getWrappers()) {
                sender.sendMessage("Wrapper: §3" + wrapper.getName() + "§7#§b" + wrapper.getSnowflake() + " §7(§e" + wrapper.getServers().size() + " servers§7)");
            }
            sender.sendMessage("----[/Wrappers]----");
        } else if (params.length == 2 && params[0].equalsIgnoreCase("stop") || params[0].equalsIgnoreCase("shutdown")) {
            try {
                String wrapperName = params[1];
                IWrapper wrapper = wrapperManager.getWrapper(wrapperName);
                if (wrapper == null) {
                    PoloLogger.print(LogLevel.WARNING, "§cThere is no Wrapper with the name §e" + wrapperName + " §cconnected!");
                    return;
                }
                wrapperName = wrapper.getName();
                if (!wrapper.terminate()) {
                    sender.sendMessage("§cCouldn't shut down §e" + wrapperName + "§c!");
                }

            } catch (Exception e) {
                //Ignoring index
            }

        } else if (params.length == 2 && params[0].equalsIgnoreCase("info")) {
            PoloLogger.print(LogLevel.INFO, "Stay tuned?!");
        }
    }


    @Override
    public List<String> onTabComplete(CommandExecutor executor, String[] args) {
        if (args.length == 0) {
            return Arrays.asList("stop", "info", "list");
        } else if (args.length == 1 && (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("info"))) {
            List<String> strings = new LinkedList<>();
            for (IWrapper wrapper : PoloCloudAPI.getInstance().getWrapperManager().getWrappers()) {
                strings.add(wrapper.getName());
            }
            return strings;
        }
        return new LinkedList<>();
    }
}
