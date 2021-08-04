package de.polocloud.plugin.protocol.property;

import com.google.common.collect.Maps;
import net.md_5.bungee.api.event.LoginEvent;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class GameServerProperty {

    private Map<Property, Object> properties;

    public GameServerProperty() {
        this.properties = Maps.newConcurrentMap();

        initialGameServerProperty();
    }

    public void initialGameServerProperty(){
        Arrays.stream(Property.values()).forEach(property -> properties.put(property, property.getObject()));
    }

    public Map<UUID, LoginEvent> getGameServerLoginEvents(){
        return (Map<UUID, LoginEvent>) properties.get(Property.LOGIN_EVENTS);
    }

    public String getGameServerMotd(){
        return (String) properties.get(Property.MOTD);
    }

    public Map<UUID, String> getGameServerLoginServers(){
        return (Map<UUID, String>) properties.get(Property.LOGIN_SERVERS);
    }

    public int getGameServerMaxPlayers(){
        return (int) properties.get(Property.MAX_PLAYERS_STATE);
    }

    public String getGameServerMaxPlayersMessage(){
        return (String) properties.get(Property.MAX_PLAYERS_MESSAGE);
    }


    public boolean isGameServerInMaintenance(){
        return (boolean) properties.get(Property.MAINTENANCE_STATE);
    }

    public String getGameServerMaintenanceMessage(){
        return (String) properties.get(Property.MAINTENANCE_MESSAGE);
    }

    public Map<Property, Object> getProperties() {
        return properties;
    }
}
