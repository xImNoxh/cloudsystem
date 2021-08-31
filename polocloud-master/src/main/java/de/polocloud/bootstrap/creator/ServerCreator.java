package de.polocloud.bootstrap.creator;

import com.google.inject.Inject;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;
import de.polocloud.api.gameserver.IGameServerManager;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.base.IWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ServerCreator {

    @Inject
    private IGameServerManager gameServerManager;

    @Inject
    private Snowflake snowflake;

    public void startServer(ITemplate template) {

        //Waiting 5 ticks to avoid starting server before stopping old one
        Scheduler.runtimeScheduler().schedule(() -> {
            IWrapper wrapper = getBestFreeWrapper(template);

            if (wrapper == null) {
                return;
            }

            long id = snowflake.nextId();
            String name = template.getName() + "-" + generateServerId(template);

            IGameServer gameServer = new SimpleGameServer(name, template.getMotd(), true, GameServerStatus.PENDING, id, -1, System.currentTimeMillis(), template.getMaxMemory(), PoloCloudAPI.getInstance().getPortManager().getPort(template), template.getMaxPlayers(), template);

            wrapper.startServer(gameServer);
        }, 5L);
    }

    private int generateServerId(ITemplate template) {
        int currentId = 1;
        boolean found = false;
        while (!found) {
            if (gameServerManager.getCached(template.getName() + "-" + currentId) == null) {
                found = true;
            } else {
                currentId++;
            }
        }
        return currentId;
    }

    public abstract boolean check(ITemplate template);

    protected IWrapper getBestFreeWrapper(ITemplate template) {
        List<IWrapper> wrapperClients = PoloCloudAPI.getInstance().getWrapperManager().getWrappers();

        if (wrapperClients.isEmpty()) return null;

        List<IWrapper> suitableWrappers = new ArrayList<>();

        wrapperClients.stream().filter(key -> Arrays.asList(template.getWrapperNames()).contains(key.getName())).forEach(suitableWrappers::add);


        if (suitableWrappers.isEmpty()) return null;
        return suitableWrappers.get(ThreadLocalRandom.current().nextInt(suitableWrappers.size()));

    }

}
