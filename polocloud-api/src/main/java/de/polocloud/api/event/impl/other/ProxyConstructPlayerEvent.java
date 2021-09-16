package de.polocloud.api.event.impl.other;

import de.polocloud.api.event.base.CloudEvent;
import de.polocloud.api.event.base.EventData;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.player.extras.IPlayerConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@EventData(nettyFire = false)
@Data @RequiredArgsConstructor
public class ProxyConstructPlayerEvent extends CloudEvent {

    /**
     * The connection that request the {@link ICloudPlayer}
     */
    private final IPlayerConnection connection;

    /**
     * The player resut to be set
     */
    private ICloudPlayer result;


}
