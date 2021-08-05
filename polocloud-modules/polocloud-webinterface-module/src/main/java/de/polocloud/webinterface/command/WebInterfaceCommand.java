package de.polocloud.webinterface.command;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.webinterface.user.WebUser;
import de.polocloud.webinterface.user.permission.WebPermission;

@CloudCommand.Info(
    name = "webinterface",
    description = "commands to manage the WebInterface",
    aliases = {"interface", "web"},
    commandType = CommandType.CONSOLE
)
public class WebInterfaceCommand extends CloudCommand {
    @Override
    public void execute(ICommandExecutor sender, String[] args) {

        if (args.length == 1) {
            Logger.log(LoggerType.INFO, "/webinterface account admin <password>");


        }else if(args.length == 5){
            WebUser user = new WebUser("admin", args[4]);

            for (WebPermission.PermissionType value : WebPermission.PermissionType.values()) {
                user.addPermission(value);
            }

            user.save();
            Logger.log(LoggerType.INFO, "admin account created");
        }

    }
}
