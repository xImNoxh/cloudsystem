package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;

public class GameServerUnregisterPacket implements IPacket {

    private String name;
    private long snowflake;
    
    public GameServerUnregisterPacket(){
        
    }
    
    public GameServerUnregisterPacket(long snowflake, String name){
        this.snowflake = snowflake;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getSnowflake() {
        return snowflake;
    }
}
