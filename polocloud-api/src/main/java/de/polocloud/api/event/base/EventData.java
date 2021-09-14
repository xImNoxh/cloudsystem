package de.polocloud.api.event.base;

import de.polocloud.api.common.PoloType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventData {

    /**
     * If de.polocloud.modules.smartproxy.packet should be globally fired
     * (Sent across the network via packets)
     */
    boolean nettyFire() default false;

    /**
     * If the event should be called and handled async
     */
    boolean async() default false;

    /**
     * If this event (if received by cloud) should
     * send the handled event back to all servers
     */
    boolean fromCloudToServers() default false;

    /**
     * The ignored types that won't receive
     * this event if its netty fired
     */
    PoloType[] ignoreTypes() default {};

}
