package de.polocloud.plugin.protocol.property;

import com.google.common.collect.Maps;

public enum Property {

    LOGIN_SERVERS(Maps.newConcurrentMap()),
    LOGIN_EVENTS(Maps.newConcurrentMap()),

    MAINTENANCE_STATE(true),
    MAINTENANCE_MESSAGE("this service is in maintenance"),

    MAX_PLAYERS_STATE(0),
    MAX_PLAYERS_MESSAGE("this service is full"),

    MOTD("The default polocloud motd");

    private Object object;

    Property(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }
}
