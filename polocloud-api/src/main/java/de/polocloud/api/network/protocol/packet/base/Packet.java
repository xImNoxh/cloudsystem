package de.polocloud.api.network.protocol.packet.base;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.protocol.IProtocolObject;
import de.polocloud.api.network.protocol.packet.base.response.def.Response;
import de.polocloud.api.network.protocol.packet.base.response.ResponseState;
import de.polocloud.api.util.Snowflake;

import java.util.function.Consumer;

/**
 * The packet class is used to send data
 * all over the network within milliseconds
 */
public abstract class Packet implements IProtocolObject {

    /**
     * The snowflake to identify this packet
     */
    protected long snowflake = Snowflake.getInstance().nextId();

    protected INetworkConnection connection;


    public void setConnection(INetworkConnection connection) {
        this.connection = connection;
    }

    public INetworkConnection getConnection() {
        return connection;
    }

    public void setSnowflake(long snowflake) {
        this.snowflake = snowflake;
    }

    public long getSnowflake() {
        return snowflake;
    }

    public void passOn() {
        this.connection.sendPacket(this);
    }

    public void passOn(PoloType... types) {
        if (types.length == 0) {
            this.connection.sendPacket(this);
            return;
        }
        for (PoloType type : types) {
            this.connection.sendPacket(this, type);
        }
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

    public void respond(ResponseState state) {
        this.respond(new Response(new JsonData(), state));
    }

    public void respond(JsonData jsonData) {
        this.respond(new Response(jsonData, ResponseState.SUCCESS));
    }

    public void respond(Consumer<JsonData> jsonData) {
        JsonData data = new JsonData();
        jsonData.accept(data);
        this.respond(new Response(data, ResponseState.SUCCESS));
    }

    public void respond(String key, Object obj) {
        this.respond(new JsonData(key, obj));
    }
}
