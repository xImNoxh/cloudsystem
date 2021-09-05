package de.polocloud.api.config.master;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.config.IConfig;
import de.polocloud.api.config.master.messages.Messages;
import de.polocloud.api.config.master.properties.Properties;
import de.polocloud.api.config.saver.IConfigSaver;
import de.polocloud.api.config.saver.SimpleConfigSaver;
import de.polocloud.api.network.packets.master.MasterUpdateConfigPacket;
import de.polocloud.api.network.protocol.packet.base.response.PacketMessenger;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.File;

public class MasterConfig implements IConfig {

    /**
     * The config properties
     */
    private final Properties properties;

    /**
     * The messages
     */
    private final Messages messages;

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
            PoloCloudAPI.getInstance().getGuice().getInstance(IConfigSaver.class).save(this, new File("config.json"));
            PoloCloudAPI.getInstance().reload();
        } else {
            PacketMessenger.newInstance().send(new MasterUpdateConfigPacket(this));
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public Messages getMessages() {
        return messages;
    }

}
