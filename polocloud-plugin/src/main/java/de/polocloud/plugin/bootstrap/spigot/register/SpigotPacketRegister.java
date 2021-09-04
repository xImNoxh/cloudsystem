package de.polocloud.plugin.bootstrap.spigot.register;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.packets.api.EventPacket;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.plugin.CloudPlugin;
import de.polocloud.plugin.protocol.register.SimplePacketRegister;

import java.util.Arrays;

public class SpigotPacketRegister {

    public SpigotPacketRegister(CloudPlugin plugin) {


        new SimplePacketRegister<EventPacket>(EventPacket.class, eventPacket -> {

            Runnable runnable = () -> {
                if (Arrays.asList(eventPacket.getIgnoredTypes()).contains(PoloCloudAPI.getInstance().getType())) {
                    return;
                }

                if(eventPacket.getExcept().equalsIgnoreCase("null")){
                    return;
                }
                PoloCloudAPI.getInstance().getEventManager().fireEventLocally(eventPacket.getEvent());
            };

            if (eventPacket.isAsync()) {
                Scheduler.runtimeScheduler().async().schedule(runnable);
            } else {
                runnable.run();
            }
        });
    }
}
