package de.polocloud.bootstrap.creator;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.GameServerStatus;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.util.Snowflake;
import de.polocloud.bootstrap.client.IWrapperClientManager;
import de.polocloud.bootstrap.client.WrapperClient;
import de.polocloud.bootstrap.gameserver.SimpleGameServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ServerCreator {

    @Inject
    private IWrapperClientManager wrapperClientManager;

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private Snowflake snowflake;

    public void startServer(ITemplate template) {
        WrapperClient client = getSuitableWrapper(template);
        if (client == null) {
            return;
        }


        long id = snowflake.nextId();
        SimpleGameServer gameServer = new SimpleGameServer(template.getName() + "-" + generateServerId(template), GameServerStatus.PENDING, null, id, template, System.currentTimeMillis());
        gameServerManager.registerGameServer(gameServer);

        client.startServer(gameServer);
    }

    private int generateServerId(ITemplate template) {
        int currentId = 1;

        boolean found = false;

        while (!found) {

            try {
                IGameServer gameServerByName = gameServerManager.getGameServerByName(template.getName() + "-" + currentId).get();

                if (gameServerByName == null) {
                    found = true;
                } else {
                    currentId++;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return currentId;
    }

    public abstract boolean check(ITemplate template);

    protected WrapperClient getSuitableWrapper(ITemplate template) {


        List<WrapperClient> wrapperClients = wrapperClientManager.getWrapperClients();

        if (wrapperClients.isEmpty()) {
            return null;
        }

        List<WrapperClient> suitableWrappers = new ArrayList<>();

        for (WrapperClient wrapperClient : wrapperClients) {
            if (Arrays.asList(template.getWrapperNames()).contains(wrapperClient.getName())) {
                suitableWrappers.add(wrapperClient);
            }
        }
        if (suitableWrappers.isEmpty()) {
            return null;
        }
        return suitableWrappers.get(ThreadLocalRandom.current().nextInt(suitableWrappers.size()));

    }

}
