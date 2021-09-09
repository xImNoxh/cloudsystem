package de.polocloud.signs.plugin.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.signs.bootstraps.PluginBootstrap;
import de.polocloud.signs.module.config.messages.SignMessagesConfig;
import de.polocloud.signs.sign.base.IGameServerSign;
import de.polocloud.signs.sign.location.SignLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Command ("cloudsigns") for managing the signs
 */
public class SignCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("§cThis command is only usable ingame§8.");
            return false;
        }
        Player player = (Player) sender;
        SignMessagesConfig messages = PluginBootstrap.getInstance().getSignService().getCurrentGlobalConfig().getSignMessages();

        if(!player.hasPermission("cloud.signs.command.use")){
            sender.sendMessage("§cYou have no permission for this command!");
            return false;
        }

        if(args.length == 1 && args[0].equals("remove")){
            Block block = player.getTargetBlock((Set<Material>) null, 5);

            if (block == null || !block.getType().equals(Material.WALL_SIGN)) {
                player.sendMessage(messages.getNoSignDetected());
                return false;
            }

            if(PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getSignByLocation(block.getLocation()) == null){
                player.sendMessage(messages.getNoSignInput());
                return false;
            }

            IGameServerSign gameServerSign = PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getSignByLocation(block.getLocation());
            if(gameServerSign != null){
                gameServerSign.cleanUp();
                PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getLoadedSigns().remove(gameServerSign);
                PluginBootstrap.getInstance().getSignService().updateSigns();
                player.sendMessage(messages.getRemoveSign());
            }
        }else if(args.length == 2 && args[0].equals("add")){
            ITemplate template = PoloCloudAPI.getInstance().getTemplateManager().getTemplate(args[1]);
            if(template == null){
                player.sendMessage(messages.getInvalidTemplate().replace("%template%", args[1]));
                return false;
            }

            Block block = player.getTargetBlock((Set<Material>) null, 5);

            if (block == null || !block.getType().equals(Material.WALL_SIGN)) {
                player.sendMessage(messages.getNoSignDetected());
                return false;
            }

            if(PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getSignByLocation(block.getLocation()) != null){
                player.sendMessage(messages.getAlreadySignDetected());
                return false;
            }


            PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getLoadedSigns().add(PluginBootstrap.getInstance().getSignService().getSignInitializer().loadSign(new SignLocation(
                block.getX(), block.getY(), block.getZ(), block.getWorld().getName(), template.getName())));
            PluginBootstrap.getInstance().getSignService().updateSigns();
            player.sendMessage(messages.getSetSign().replace("%template%", template.getName()));
        }else{
            player.sendMessage(messages.getCommandHelp());
        }

        return false;
    }
}
