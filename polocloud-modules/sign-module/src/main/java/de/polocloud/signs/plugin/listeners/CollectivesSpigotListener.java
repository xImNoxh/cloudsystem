package de.polocloud.signs.plugin.listeners;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.signs.bootstraps.PluginBootstrap;
import de.polocloud.signs.sign.base.IGameServerSign;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CollectivesSpigotListener implements Listener {

    @EventHandler
    public void handle(SignChangeEvent event){
        Sign sign = (Sign) event.getBlock().getState();
        IGameServerSign gameServerSign = PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getGameSignBySign(sign);
        if(gameServerSign != null){
            gameServerSign.reloadSign(sign);
            gameServerSign.updateSign();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handle(PlayerInteractEvent event) {
        if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)))
            return;
        if (event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.WALL_SIGN)) return;

        IGameServerSign gameServerSign = PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getSignByLocation(event.getClickedBlock().getLocation());
        if (gameServerSign == null || gameServerSign.getGameServer() == null) return;
        Player player = event.getPlayer();
        if (gameServerSign.getTemplate().isMaintenance()) {
            player.sendMessage(PluginBootstrap.getInstance().getSignService().getCurrentGlobalConfig().getSignMessages().getMaintenanceConnected());
            return;
        }
        ICloudPlayer cloudPlayer = PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(player.getName());
        if (cloudPlayer.getMinecraftServer().getSnowflake() == gameServerSign.getGameServer().getSnowflake()) {
            player.sendMessage(PluginBootstrap.getInstance().getSignService().getCurrentGlobalConfig().getSignMessages().getAlreadySameService());
            return;
        }
        if(gameServerSign.getGameServer().getOnlinePlayers() >= gameServerSign.getGameServer().getMaxPlayers()){
            player.sendMessage(PluginBootstrap.getInstance().getSignService().getCurrentGlobalConfig().getSignMessages().getConnectedIfServiceIsFull());
            return;
        }
        cloudPlayer.sendTo(gameServerSign.getGameServer());
    }

}
