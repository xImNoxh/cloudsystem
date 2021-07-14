package de.polocloud.bootstrap.creator;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.ITemplate;
import de.polocloud.bootstrap.client.WrapperClient;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SimpleServerCreator extends ServerCreator {

    @Inject
    private IGameServerManager gameServerManager;

    @Override
    public boolean check(ITemplate template) {
        List<IGameServer> serversByTemplate = null;
        try {
            serversByTemplate = gameServerManager.getGameServersByTemplate(template).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        if (serversByTemplate == null) {
            return false;
        }
        return serversByTemplate.size() < template.getMinServerCount();
    }

}
