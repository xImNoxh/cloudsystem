package de.polocloud.bootstrap.creator;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.util.Snowflake;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.WrapperClient;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;

import java.util.concurrent.ThreadLocalRandom;

public abstract class ServerCreator {

    @Inject
    private IWrapperClientManager wrapperClientManager;

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private Snowflake snowflake;

    public void startServer(ITemplate template) {
        WrapperClient client = getSuitableWrapper();
        if (client == null) {
            return;
        }


        long id = snowflake.nextId();
        SimpleGameServer gameServer = new SimpleGameServer(template.getName() + "-" + id, GameServerStatus.PENDING, null, id, template, System.currentTimeMillis());
        gameServerManager.registerGameServer(gameServer);

        client.startServer(gameServer);
    }

    public abstract boolean check(ITemplate template);

    private WrapperClient getSuitableWrapper() {
        if (wrapperClientManager.getWrapperClients().isEmpty()) {
            return null;
        }
        return wrapperClientManager.getWrapperClients().get(ThreadLocalRandom.current().nextInt(wrapperClientManager.getWrapperClients().size()));
    }

}
