package de.polocloud.tablist.attribute;

import com.google.common.collect.Maps;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.bootstrap.Master;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class AttributeConverter {

    private static Map<AttributeKeys, Attribute> attributes;

    public AttributeConverter() {
        attributes = Maps.newConcurrentMap();

        attributes.put(AttributeKeys.ONLINE_COUNT, new Attribute() {
            @Override
            public Object execute(ICloudPlayer player) {
                try {
                    return Master.getInstance().getCloudPlayerManager().getAllOnlinePlayers().get().size();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });
        attributes.put(AttributeKeys.MAX_PLAYERS, new Attribute() {
            @Override
            public Object execute(ICloudPlayer player) {
                return player.getProxyServer().getTemplate().getMaxPlayers();
            }
        });

        attributes.put(AttributeKeys.PROXY_NAME, new Attribute() {
            @Override
            public Object execute(ICloudPlayer player) {
                return player.getProxyServer().getName();
            }
        });
        attributes.put(AttributeKeys.SERVICE_NAME, new Attribute() {
            @Override
            public Object execute(ICloudPlayer player) {
                return (player.getMinecraftServer() != null)  ? player.getMinecraftServer().getName() : "null";
            }
        });
    }

    public static String[] convertTab(String header, String footer, ICloudPlayer player){
        for(AttributeKeys key : AttributeKeys.values()){
            header = header.replaceAll(key.getDisplay(), attributes.get(key).execute(player).toString());
            footer = footer.replaceAll(key.getDisplay(), attributes.get(key).execute(player).toString());
        }
        return new String[]{header, footer};
    }

    public Attribute getAttributes(AttributeKeys attributeKeys) {
        return attributes.get(attributeKeys);
    }

}
