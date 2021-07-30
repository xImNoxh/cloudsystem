package de.polocloud.signs.collectives;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.plugin.api.CloudExecutor;
import de.polocloud.plugin.api.spigot.event.CloudPlayerSwitchServerEvent;
import de.polocloud.plugin.api.spigot.event.CloudServerStartedEvent;
import de.polocloud.plugin.api.spigot.event.CloudServerStoppedEvent;
import de.polocloud.plugin.api.spigot.event.CloudServerUpdatedEvent;
import de.polocloud.signs.SignService;
import de.polocloud.signs.bootstrap.SignBootstrap;
import de.polocloud.signs.signs.IGameServerSign;
import de.polocloud.signs.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CollectiveSpigotListener implements Listener {

    private SignService signService;

    public CollectiveSpigotListener() {
        signService = SignService.getInstance();
        Bukkit.getPluginManager().registerEvents(this, SignBootstrap.getInstance());
    }

    @EventHandler
    public void handle(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.WALL_SIGN)) return;
        Sign sign = (Sign) event.getClickedBlock().getState();
        IGameServerSign gameSign = signService.getCache().stream().filter(s -> s.getSign().equals(sign)).findAny().orElse(null);
        if (gameSign == null || gameSign.getGameServer() == null) return;

        Player player = event.getPlayer();
        CloudExecutor.getInstance().getCloudPlayerManager().getOnlinePlayer(player.getUniqueId()).thenAccept(key -> {
            if (key.getMinecraftServer().getSnowflake() == gameSign.getGameServer().getSnowflake()) {
                player.sendMessage(signService.getSignConfig().getSignMessages().getAlreadySameService());
                return;
            }
            PlayerUtils.sendService(gameSign.getGameServer(), player);
        });
    }

    @EventHandler
    public void handle(SignChangeEvent event) {
        Sign sign = (Sign) event.getBlock().getState();
        IGameServerSign gameSign = signService.getCache().stream().filter(s -> s.getLocation().equals(sign.getLocation())).findAny().get();
        if (gameSign == null) return;
        gameSign.reloadSign(sign);
        gameSign.updateSign();
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(CloudServerStartedEvent event) {
        IGameServerSign sign = signService.getFreeTemplateSign(event.getGameServer());
        sign.setGameServer(event.getGameServer());
        sign.displayService();
    }

    @EventHandler
    public void handle(CloudPlayerSwitchServerEvent event) {
        event.getTo().thenAccept(service -> {
            IGameServerSign gameServerSign = signService.getCache().stream().filter(key ->
                key.getGameServer().getSnowflake() == service.getSnowflake()).findAny().get();
            if (gameServerSign == null) return;
            gameServerSign.setGameServer(service);
            gameServerSign.displayService();
        });

        event.getFrom().thenAccept(service -> {
            IGameServerSign gameServerSign = signService.getCache().stream().filter(key ->
                key.getGameServer().getSnowflake() == service.getSnowflake()).findAny().get();
            if (gameServerSign == null) return;
            gameServerSign.setGameServer(service);
            gameServerSign.displayService();
        });
    }

    @EventHandler
    public void handle(CloudServerUpdatedEvent event) {
        IGameServerSign gameServerSign = signService.getCache().stream().filter(key ->
            key.getGameServer().getSnowflake() == event.getGameServer().getSnowflake()).findAny().get();
        if (gameServerSign == null) return;
        gameServerSign.setGameServer(event.getGameServer());
        gameServerSign.displayService();
    }


    @EventHandler
    public void handle(CloudServerStoppedEvent event) {
        IGameServer gameServer = event.getGameServer();
        IGameServerSign gameServerSign = SignService.getInstance().getCache().stream().filter(key ->
            key.getGameServer().getSnowflake() == gameServer.getSnowflake()).findAny().get();
        if (gameServerSign == null) return;
        gameServerSign.setGameServer(null);
        gameServerSign.writeSign();

    }

}
