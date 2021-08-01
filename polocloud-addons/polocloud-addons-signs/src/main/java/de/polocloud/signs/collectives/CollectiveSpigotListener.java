package de.polocloud.signs.collectives;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.plugin.api.CloudExecutor;
import de.polocloud.plugin.api.spigot.event.*;
import de.polocloud.signs.SignService;
import de.polocloud.signs.bootstrap.SignBootstrap;
import de.polocloud.signs.config.SignConfig;
import de.polocloud.signs.executes.ExecuteService;
import de.polocloud.signs.signs.IGameServerSign;
import de.polocloud.signs.signs.cache.IGameServerSignCache;
import de.polocloud.signs.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.stream.Collectors;

public class CollectiveSpigotListener implements Listener {

    private SignService signService;
    private SignConfig signConfig;
    private IGameServerSignCache cache;
    private ExecuteService executeService;

    public CollectiveSpigotListener() {

        this.signService = SignService.getInstance();
        this.signConfig = signService.getSignConfig();
        this.cache = signService.getCache();
        this.executeService = signService.getExecuteService();

        Bukkit.getPluginManager().registerEvents(this, SignBootstrap.getInstance());
    }

    @EventHandler
    public void handle(PlayerInteractEvent event) {
        if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)))
            return;
        if (event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.WALL_SIGN)) return;
        IGameServerSign gameSign = cache.stream().filter(s -> s.getSign().equals(event.getClickedBlock().getState())).findAny().orElse(null);
        if (gameSign == null || gameSign.getGameServer() == null) return;
        Player player = event.getPlayer();
        if (gameSign.getTemplate().isMaintenance()) {
            player.sendMessage(signConfig.getSignMessages().getMaintenanceConnected());
            return;
        }
        CloudExecutor.getInstance().getCloudPlayerManager().getOnlinePlayer(player.getUniqueId()).thenAccept(key -> {
            if (key.getMinecraftServer().getSnowflake() == gameSign.getGameServer().getSnowflake()) {
                player.sendMessage(signService.getSignConfig().getSignMessages().getAlreadySameService());
                return;
            }
            if (!signConfig.isConnectIfFull()) {
                if (signConfig.isCanUseConnectIfFullPermission()) {
                    if (player.hasPermission(signConfig.getConnectIfFullPermission())) {
                        PlayerUtils.sendService(gameSign.getGameServer(), player);
                        return;
                    }
                }
                player.sendMessage(signConfig.getSignMessages().getConnectedIfServiceIsFull());
                return;
            }
            PlayerUtils.sendService(gameSign.getGameServer(), player);
        });
    }

    @EventHandler
    public void handle(SignChangeEvent event) {
        Sign sign = (Sign) event.getBlock().getState();
        IGameServerSign gameSign = executeService.getServiceInspectExecute().getGameSignBySign(sign);
        if (gameSign == null) return;
        gameSign.reloadSign(sign);
        gameSign.updateSign();
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(CloudServerStartedEvent event) {
        executeService.getServiceUpdateExecute().update(executeService.getServiceInspectExecute().getFreeTemplateSign(event.getGameServer()), event.getGameServer());
    }

    @EventHandler
    public void handle(CloudPlayerSwitchServerEvent event) {
        event.getTo().thenAccept(service -> executeService.getServiceUpdateExecute().update( executeService.getServiceInspectExecute().execute(service), service));
        event.getFrom().thenAccept(service -> executeService.getServiceUpdateExecute().update( executeService.getServiceInspectExecute().execute(service), service));
    }

    @EventHandler
    public void handle(CloudServerUpdatedEvent event) {
        executeService.getServiceUpdateExecute().update(executeService.getServiceInspectExecute().execute(event.getGameServer()), event.getGameServer());
    }

    @EventHandler
    public void handle(TemplateMaintenanceUpdateEvent event) {
        List<IGameServerSign> gameServerSign = cache.stream().filter(key -> key.getTemplate().getName().equals(event.getTemplate().getName())).collect(Collectors.toList());
        for (IGameServerSign sign : gameServerSign) {
            sign.setTemplate(event.getTemplate());
            sign.writeSign(false);
        }
    }

    @EventHandler
    public void handle(CloudServerStoppedEvent event) {
        IGameServerSign gameServerSign = executeService.getServiceInspectExecute().execute(event.getGameServer());
        if (gameServerSign == null) return;
        gameServerSign.setGameServer(null);
        gameServerSign.writeSign(false);
    }

}
