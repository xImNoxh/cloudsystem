package de.polocloud.modules.proxy.command;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.annotation.CommandExecutors;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.identifier.CommandListener;

public class ProxyCommand implements CommandListener {

    @Command(
        name = "proxy",
        aliases = {"pr"},
        description = "Manages the Proxy module"
    )
    @CommandExecutors(ExecutorType.ALL)
  //  @CommandPermission("cloud.proxy")
    public void execute(CommandExecutor executor, String[] fullArgs, String... args) {

        String prefix = PoloCloudAPI.getInstance().getMasterConfig().getMessages().getPrefix();


        executor.sendMessage(prefix + "§b§owhitelist list §8- §7List all whitelist players§8.");
        executor.sendMessage(prefix + "§b§owhitelist reset §8- §7Reset alle players§8.");
        executor.sendMessage(prefix + "§b§owhitelist add (name) §8- §7Add a player to the whitelist§8.");
        executor.sendMessage(prefix + "§b§owhitelist remove (name) §8- §7Remove a player from the the whitelist§8.");

    }

}
