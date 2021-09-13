package de.polocloud.api.config.master;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.IConfig;
import de.polocloud.api.config.master.messages.Messages;
import de.polocloud.api.config.master.properties.Properties;
import de.polocloud.api.network.packets.master.MasterUpdateConfigPacket;
import de.polocloud.api.network.protocol.IProtocolObject;
import de.polocloud.api.network.protocol.buffer.IPacketBuffer;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;

import java.io.IOException;

public class MasterConfig implements IConfig, IProtocolObject {

    /**
     * The config properties
     */
    private Properties properties;

    /**
     * The messages
     */
    private Messages messages;

    public MasterConfig() {
        this.messages = new Messages();
        this.properties = new Properties();
    }

    /**
     * Updates this config instance and syncs it all over the network
     */
    public void update() {
        PoloCloudAPI.getInstance().setMasterConfig(this);

        if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
            PoloCloudAPI.getInstance().getConfigSaver().save(this, FileConstants.MASTER_CONFIG_FILE);
            PoloCloudAPI.getInstance().reload();
        } else {
            PacketMessenger.create().send(new MasterUpdateConfigPacket(this));
        }
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public Properties getProperties() {
        return properties;
    }

    public Messages getMessages() {
        return messages;
    }

    @Override
    public void write(IPacketBuffer buf) throws IOException {
        buf.writeProtocol(this.properties);
        buf.writeProtocol(this.messages);
    }

    @Override
    public void read(IPacketBuffer buf) throws IOException {
        this.properties = buf.readProtocol();
        this.messages = buf.readProtocol();
    }
}
