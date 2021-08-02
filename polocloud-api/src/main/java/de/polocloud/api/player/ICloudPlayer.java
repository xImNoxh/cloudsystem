package de.polocloud.api.player;

import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.common.INamable;
import de.polocloud.api.gameserver.IGameServer;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ICloudPlayer extends INamable, Serializable, ICommandExecutor {

    UUID getUUID();

    IGameServer getProxyServer();

    IGameServer getMinecraftServer();

    void sendMessage(String message);

    void sendTo(IGameServer gameServer);

    void sendTablist(String header, String footer);

    CompletableFuture<Boolean> hasPermissions(String permission);

    void kick(String message);

    void sendToFallback();

}
