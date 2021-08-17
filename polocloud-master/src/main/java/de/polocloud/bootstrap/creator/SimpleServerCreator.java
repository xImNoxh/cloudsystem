package de.polocloud.bootstrap.creator;

import com.google.inject.Inject;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.template.ITemplate;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;

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
        if (template.isStatic()) {
            return false;
        }

        if (template.getMaxServerCount() <= serversByTemplate.size()) {
            return false;
        }

        if (serversByTemplate.size() < template.getMinServerCount()) {
            return true;
        }


        int onlinePlayers = 0;
        int totalMaxPlayers = serversByTemplate.size() * template.getMaxPlayers();

        float percentage;

        for (IGameServer gameServer : serversByTemplate) {
            onlinePlayers += gameServer.getOnlinePlayers();
        }

        percentage = (onlinePlayers * 100.0F) / (totalMaxPlayers);

        if (percentage >= template.getServerCreateThreshold()) {
            Logger.log(LoggerType.INFO, "Group " + template.getName() + " is " + percentage + "% full! " + "(" + template.getServerCreateThreshold() + "% required)");
            return true;
        }


        return false;

    }

}
