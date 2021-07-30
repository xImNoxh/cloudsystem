package de.polocloud.signs.commands;

import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.plugin.api.CloudExecutor;
import de.polocloud.signs.SignService;
import de.polocloud.signs.config.messages.SignMessages;
import de.polocloud.signs.executes.ExecuteService;
import de.polocloud.signs.signs.ConfigSignLocation;
import de.polocloud.signs.signs.IGameServerSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class CloudSignsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        SignMessages signMessages = SignService.getInstance().getSignConfig().getSignMessages();
        if (args.length == 2 && (args[0].equalsIgnoreCase("add"))) {

            Block block = player.getTargetBlock((Set<Material>) null, 5);

            String group = args[1];

            if (block == null || !block.getType().equals(Material.WALL_SIGN)) {
                player.sendMessage(signMessages.getNoSignDetected());
                return false;
            }


            if (SignService.getInstance().getCache().alreadySign(block.getLocation())) {
                player.sendMessage(signMessages.getAlreadySignDetected());
                return false;
            }

            player.sendMessage(signMessages.getSetSign().replace("%template%", group));

            SignService.getInstance().getSignConfig().getLocationConfig().getLocations().add(new ConfigSignLocation(
                block.getX(), block.getY(), block.getZ(), block.getWorld().getName(), group));
            IConfigSaver configSaver = SignService.getInstance().getConfigSaver();
            configSaver.save(SignService.getInstance().getSignConfig(), new File("config.json"));

            //adding direct a sign

            CloudExecutor.getInstance().getTemplateService().getTemplateByName(group).thenAccept(key -> {
                try {
                    ExecuteService service = SignService.getInstance().getExecuteService();
                    IGameServer gameServer = service.getServiceInspectExecute().getGameServerWithNoSign(key);
                    if(gameServer == null) return;
                    service.getServiceUpdateExecute().update(service.getServiceInspectExecute().getFreeTemplateSign(gameServer), gameServer);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {

            Block block = player.getTargetBlock((Set<Material>) null, 5);

            if (block == null || !block.getType().equals(Material.WALL_SIGN)) {
                player.sendMessage(signMessages.getNoSignDetected());
                return false;
            }


            if (!SignService.getInstance().getCache().alreadySign(block.getLocation())) {
                player.sendMessage(signMessages.getNoSignInput());
                return false;
            }

            IGameServerSign gameSign = SignService.getInstance().getCache().stream().filter(s -> s.getLocation().equals(block.getLocation())).findAny().orElse(null);
            gameSign.clean();


            IConfigSaver configSaver = SignService.getInstance().getConfigSaver();
            SignService.getInstance().getSignConfig().getLocationConfig().getLocations().remove(gameSign);
            configSaver.save(SignService.getInstance().getSignConfig(), new File("config.json"));
            SignService.getInstance().getCache().remove(gameSign);

            player.sendMessage(signMessages.getRemoveSign());
            return false;

        }

        player.sendMessage(signMessages.getCommandHelp());
        return false;
    }
}
