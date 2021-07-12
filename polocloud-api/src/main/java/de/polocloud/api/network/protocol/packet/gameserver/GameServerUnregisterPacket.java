package de.polocloud.api.network.protocol.packet.gameserver;

import de.polocloud.api.network.protocol.packet.IPacket;

public class GameServerUnregisterPacket implements IPacket {

    private long snowflake;
    
    public GameServerUnregisterPacket(){
        
    }
    
    public GameServerUnregisterPacket(long snowflake){
        this.snowflake = snowflake;
    }

    public long getSnowflake() {
        return snowflake;
    }
}
