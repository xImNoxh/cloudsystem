package de.polocloud.modules.proxy.command;

import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.annotation.CommandExecutors;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.player.ICloudPlayer;

public class ProxyCommand implements CommandListener {

    public ProxyCommand() {
    }

    @Command(name = "cloud", description = "Manage the proxy module", aliases = "pr")
    @CommandExecutors(ExecutorType.PLAYER)
    public void execute(CommandExecutor executor, String[] fullArgs, String... args) {
        ICloudPlayer cloudPlayer = (ICloudPlayer) executor;



    }

}
