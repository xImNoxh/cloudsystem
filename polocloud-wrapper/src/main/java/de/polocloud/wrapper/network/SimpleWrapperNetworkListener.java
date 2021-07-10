/*package de.polocloud.wrapper.network;

import com.google.inject.Inject;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.packet.IPacket;
import de.polocloud.api.network.protocol.packet.wrapper.WrapperLoginPacket;

public class SimpleWrapperNetworkListener implements NetworkListener {

    public IProtocol protocol;

    public SimpleWrapperNetworkListener(IProtocol protocol){
        this.protocol = protocol;
    }

    @NetworkHandler
    public void handle(ConnectEvent event){

        event.getCtx().writeAndFlush(new WrapperLoginPacket("xXxPoloxXxCloudxXx"));
    }

    @NetworkHandler
    public void handle(ReceiveEvent event) {
        if (event.getObject() instanceof IPacket) {
            protocol.firePacketHandlers(event.getCtx(), (IPacket) event.getObject());
        }
    }


}
*/
