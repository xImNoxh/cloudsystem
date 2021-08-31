package de.polocloud.example.command;

import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.example.ExampleModule;

public class ExampleCommand implements CommandListener {

    @Command(
        name = "test",
        aliases = "t",
        usage = "",
        description = "None"
    )
    public void execute(CommandExecutor executor, String[] fullArgs, String... args) {
        executor.sendMessage(ExampleModule.getInstance().getConfig().toString());
    }
}
