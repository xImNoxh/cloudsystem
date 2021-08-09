package de.polocloud.plugin.protocol.property;

import com.google.common.collect.Maps;

public enum Property {

    LOGIN_SERVERS(Maps.newConcurrentMap()),
    LOGIN_EVENTS(Maps.newConcurrentMap()),

    MAINTENANCE_MESSAGE("this service is in maintenance"),

    MAX_PLAYERS_MESSAGE("this service is full");

    private Object object;

    Property(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }
}
