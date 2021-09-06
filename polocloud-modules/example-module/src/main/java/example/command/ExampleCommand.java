package example.command;

import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.player.ICloudPlayer;

public class ExampleCommand implements CommandListener {

    @Command(
        name = "example",
        aliases = {"ex","alias"},
        usage = "",
        description = "None"
    )
    public void execute(CommandExecutor executor, String[] fullArgs, String... args) {
        executor.sendMessage("test2");
        if(!executor.hasPermission("your.permission")){
            executor.sendMessage("test1");
            return;
        }

        ICloudPlayer cloudPlayer = (ICloudPlayer) executor;

    }
}
