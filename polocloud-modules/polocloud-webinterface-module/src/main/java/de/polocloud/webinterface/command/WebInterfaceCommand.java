package de.polocloud.webinterface.command;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.webinterface.WebInterfaceModule;
import de.polocloud.webinterface.config.WebInterfaceConfig;
import de.polocloud.webinterface.user.WebUser;
import de.polocloud.webinterface.user.permission.WebPermission;

import java.security.spec.InvalidKeySpecException;

@CloudCommand.Info(
    name = "webinterface",
    description = "commands to manage the WebInterface",
    aliases = {"interface", "web"},
    commandType = CommandType.CONSOLE
)
public class WebInterfaceCommand extends CloudCommand {

    private WebInterfaceConfig config;

    public WebInterfaceCommand(WebInterfaceConfig config) {
        this.config = config;
    }

    @Override
    public void execute(ICommandExecutor sender, String[] args) {

        if (args.length == 1) {
            Logger.log(LoggerType.INFO, "/webinterface account create admin <password>");
            Logger.log(LoggerType.INFO, "/webinterface account verify admin <password>");


        } else if (args.length == 5 && args[1].equalsIgnoreCase("account") && args[2].equals("create") && args[3].equalsIgnoreCase("admin")) {
            String unhashedPassword = args[4];
            WebUser user = null;
            try {
                user = new WebUser("admin", WebInterfaceModule.getInstance().getPasswordManager()
                    .generateHash(unhashedPassword, this.config.getSecurity().getSalt()));
                user.enableAccount();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                return;
            }

            for (WebPermission.PermissionType value : WebPermission.PermissionType.values()) {
                user.addPermission(value);
            }

            user.save();
            Logger.log(LoggerType.INFO, "admin account created");
        }

    }
}
