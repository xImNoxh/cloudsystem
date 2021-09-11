package de.polocloud.bootstrap.creator;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.SimpleGameServer;
import de.polocloud.api.gameserver.helper.GameServerStatus;

import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.wrapper.base.IWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ServerCreator {

    public void startServer(ITemplate template) {

        //Waiting 5 ticks to avoid starting server before stopping old one
        Scheduler.runtimeScheduler().schedule(() -> {
            try {

                IWrapper wrapper = getBestFreeWrapper(template);

                if (wrapper == null) {
                    return;
                }

                long id = Snowflake.getInstance().nextId();
                int port = PoloCloudAPI.getInstance().getGameServerManager().getFreePort(template);
                int intId = PoloCloudAPI.getInstance().getGameServerManager().getFreeId(template);

                wrapper.startServer(new SimpleGameServer(intId, template.getMotd(), true, GameServerStatus.STARTING, id,  System.currentTimeMillis(), template.getMaxMemory(), port, template.getMaxPlayers(), template.getName()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 5L);
    }

    public abstract boolean needsNewServer(ITemplate template);

    protected IWrapper getBestFreeWrapper(ITemplate template) {
        List<IWrapper> wrapperClients = PoloCloudAPI.getInstance().getWrapperManager().getWrappers();

        if (wrapperClients.isEmpty()) return null;

        List<IWrapper> suitableWrappers = new ArrayList<>();

        wrapperClients.stream().filter(key -> Arrays.asList(template.getWrapperNames()).contains(key.getName())).forEach(suitableWrappers::add);


        if (suitableWrappers.isEmpty()) return null;
        return suitableWrappers.get(ThreadLocalRandom.current().nextInt(suitableWrappers.size()));

    }

}
