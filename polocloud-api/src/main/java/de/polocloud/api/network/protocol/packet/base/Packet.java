package de.polocloud.api.network.protocol.packet.base;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.network.protocol.IProtocolObject;
import de.polocloud.api.network.protocol.packet.base.response.Response;
import de.polocloud.api.util.Snowflake;

/**
 * The packet class is used to send data
 * all over the network within milliseconds
 */
public abstract class Packet implements IProtocolObject {

    /**
     * The snowflake to identify this packet
     */
    protected long snowflake = Snowflake.getInstance().nextId();

    public void setSnowflake(long snowflake) {
        this.snowflake = snowflake;
    }

    public long getSnowflake() {
        return snowflake;
    }

    /**
     * Responds to this packet
     *
     * @param response the response to send
     */
    public void respond(Response response) {
        response.setSnowflake(this.snowflake);
        PoloCloudAPI.getInstance().sendPacket(response);
    }

}
