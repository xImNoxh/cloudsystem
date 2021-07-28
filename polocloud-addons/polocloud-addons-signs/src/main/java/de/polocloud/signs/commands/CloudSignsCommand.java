package de.polocloud.signs.commands;

import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.signs.SignService;
import de.polocloud.signs.signs.ConfigSignLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Set;

public class CloudSignsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        Block block = player.getTargetBlock((Set<Material>) null, 3);

        String group = args[0];

        if(block == null){
            player.sendMessage("no block");
            return false;
        }

        if(!block.getType().equals(Material.WALL_SIGN)){
           player.sendMessage("no wall sign");
            return false;
        }

        player.sendMessage("adding sign " + group);


        SignService.getInstance().getSignConfig().getLocationConfig().getLocations().add(new ConfigSignLocation(
            block.getX(), block.getY(), block.getZ(), block.getWorld().getName(), group));
        IConfigSaver configSaver = SignService.getInstance().getConfigSaver();
        configSaver.save(SignService.getInstance().getSignConfig(), new File("config.json"));



        return false;
    }
}
