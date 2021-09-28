package de.polocloud.api.network.protocol.packet.base;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.network.INetworkConnection;
import de.polocloud.api.network.protocol.IProtocolObject;
import de.polocloud.api.network.protocol.packet.base.response.extra.INetworkResponse;
import de.polocloud.api.network.protocol.packet.base.response.base.IResponse;
import de.polocloud.api.network.protocol.packet.base.response.def.Response;
import de.polocloud.api.network.protocol.packet.base.response.ResponseState;
import de.polocloud.api.network.protocol.packet.base.response.extra.SimpleNetworkResponse;
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

    /**
     * Responds to this packet
     *
     * @param response the response to send
     */
    public void respond(IResponse response) {
        ((Response)response).setSnowflake(this.snowflake);
        PoloCloudAPI.getInstance().sendPacket((Packet) response);
    }

    public void createResponse(Consumer<INetworkResponse> response) {
        SimpleNetworkResponse networkResponse = new SimpleNetworkResponse();
        response.accept(networkResponse);
        this.respond(new Response(new JsonData().append("_element", networkResponse.getElement()).append("_throwable", networkResponse.getError()).append("_success", networkResponse.isSuccess()), networkResponse.getStatus()));
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
