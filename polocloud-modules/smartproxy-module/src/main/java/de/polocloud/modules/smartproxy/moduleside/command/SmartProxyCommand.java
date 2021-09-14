package de.polocloud.modules.smartproxy.moduleside.command;

import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.modules.smartproxy.moduleside.SmartProxy;
import de.polocloud.modules.smartproxy.moduleside.config.SmartProxyConfig;

public class SmartProxyCommand implements CommandListener {

    @Command(
        name = "smartProxy",
        aliases = {"sp", "proxy"},
        description = "Manages this module"
    )
    public void execute(CommandExecutor executor, String[] fullArgs, String... args) {
        SmartProxyConfig config = SmartProxy.getInstance().getSmartProxyConfig();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("toggle")) {
                boolean toggle = !config.isEnabled();
                config.setEnabled(toggle);
                config.update();

                PoloLogger.print(LogLevel.INFO, "§7SmartProxy is now " + (toggle ? "§aenabled" : "§cdisabled") + "§7!");
                return;
            }
            if (args[0].equalsIgnoreCase("switchMode")) {
                String mode = config.getProxySearchMode();

                if (mode == null) {
                    mode = "RANDOM";
                }

                if (mode.equalsIgnoreCase("RANDOM")) {
                    mode = "FILL";
                } else if (mode.equalsIgnoreCase("FILL")) {
                    mode = "BALANCED";
                } else {
                    mode = "RANDOM";
                }

                SmartProxyConfig smartProxyConfig = SmartProxy.getInstance().getSmartProxyConfig();
                smartProxyConfig.setProxySearchMode(mode);
                smartProxyConfig.update();
                PoloLogger.print(LogLevel.INFO, "§7SmartProxy now searches for free proxies with §e" + mode.toUpperCase() + "-Mode§7!");
                return;
            }
        }
        PoloLogger.print(LogLevel.INFO, "----[SmartProxy]----");
        PoloLogger.print(LogLevel.INFO, "Use §bsmartproxy <toggle> §7to toggle this system");
        PoloLogger.print(LogLevel.INFO, "Use §bsmartproxy <switchMode> §7to switch the ProxySearchMode");
        PoloLogger.print(LogLevel.INFO, "----[/SmartProxy]----");
    }
}
