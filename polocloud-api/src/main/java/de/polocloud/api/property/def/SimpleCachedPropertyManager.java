package de.polocloud.api.property.def;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.common.PoloType;
import de.polocloud.api.common.PoloTypeUnsupportedActionException;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.network.packets.property.PropertyClearPacket;
import de.polocloud.api.network.packets.property.PropertyDeletePacket;
import de.polocloud.api.network.packets.property.PropertyInsertPacket;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.IPropertyManager;
import de.polocloud.api.scheduler.Scheduler;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SimpleCachedPropertyManager implements IPropertyManager {

    private Map<UUID, List<IProperty>> properties;

    public SimpleCachedPropertyManager() {
        this.properties = new ConcurrentHashMap<>();

        Scheduler.runtimeScheduler().schedule(() -> {
            PoloCloudAPI.getInstance().registerSimplePacketHandler(PropertyInsertPacket.class, packet -> {

                IProperty property = packet.getProperty();
                UUID uniqueId = packet.getUniqueId();

                List<IProperty> properties = getProperties(uniqueId);
                properties.add(property);
                this.properties.put(uniqueId, properties);

                if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
                    save(uniqueId);
                    PoloCloudAPI.getInstance().sendPacket(packet);
                }
            });

            PoloCloudAPI.getInstance().registerSimplePacketHandler(PropertyDeletePacket.class, packet -> {

                String property = packet.getName();
                UUID uniqueId = packet.getUniqueId();

                List<IProperty> properties = getProperties(uniqueId);
                properties.removeIf(p -> p.getName().equalsIgnoreCase(property));
                this.properties.put(uniqueId, properties);

                if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
                    save(uniqueId);
                    PoloCloudAPI.getInstance().sendPacket(packet);
                }
            });

            PoloCloudAPI.getInstance().registerSimplePacketHandler(PropertyClearPacket.class, packet -> {

                UUID uniqueId = packet.getUniqueId();

                properties.remove(uniqueId);
                if (PoloCloudAPI.getInstance().getType() == PoloType.MASTER) {
                    PoloCloudAPI.getInstance().sendPacket(packet);
                }
            });
        }, () -> PoloCloudAPI.getInstance().getConnection() != null);
    }


    @Override
    public boolean loadProperties(UUID uniqueId) throws PoloTypeUnsupportedActionException {
        File file = new File(FileConstants.MASTER_PLAYER_PROPERTIES, uniqueId + ".json");
        JsonData jsonData = new JsonData(file);
        JsonData singleProperties = jsonData.fallback(new JsonData()).getData("singleProperties");
        JsonData multiProperties = jsonData.fallback(new JsonData()).getData("multiProperties");

        List<IProperty> props = new ArrayList<>();
        for (String key : singleProperties.keySet()) {
            JsonData sub = singleProperties.getData(key);
            IProperty property = new SimpleProperty(sub.getString("name"), new ArrayList<>(), sub.getElement("value"));
            props.add(property);
        }

        props.addAll(this.getProperty(multiProperties));

        //Clearing old properties
        properties.remove(uniqueId);
        PoloCloudAPI.getInstance().sendPacket(new PropertyClearPacket(uniqueId));

        for (IProperty prop : props) {
            PropertyInsertPacket packet = new PropertyInsertPacket(prop, uniqueId);
            PoloCloudAPI.getInstance().sendPacket(packet);
        }

        this.properties.put(uniqueId, props);
        return true;
    }

    private List<SimpleProperty> getProperty(JsonData data) {
        List<SimpleProperty> list = new ArrayList<>();
        for (String key : data.keySet()) {
            JsonData sub = data.getData(key);
            if (sub.has("value") && sub.has("name")) {
                SimpleProperty property = sub.getAs(SimpleProperty.class);
                list.add(property);
            } else {
                List<SimpleProperty> property = getProperty(sub);
                SimpleProperty p = new SimpleProperty(key, property, null);
                list.add(p);
            }
        }
        return list;
    }

    @Override
    public List<IProperty> getProperties(UUID uniqueId) {
        return this.properties.getOrDefault(uniqueId, new ArrayList<>());
    }

    @Override
    public IProperty getProperty(UUID uniqueId, String name) {
        return this.getProperties(uniqueId).stream().filter(property -> property.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public void save(UUID uniqueId) throws PoloTypeUnsupportedActionException {

        if (PoloCloudAPI.getInstance().getType() != PoloType.MASTER) {
            throw new PoloTypeUnsupportedActionException("PoloInstances of type " + PoloCloudAPI.getInstance().getType() + " do not support saving Properties!");
        }

        File directory = FileConstants.MASTER_PLAYER_PROPERTIES;

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, uniqueId + ".json");


        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JsonData jsonData = new JsonData(file);
        JsonData singleProperties = jsonData.fallback(new JsonData()).getData("singleProperties");
        JsonData multiProperties = jsonData.fallback(new JsonData()).getData("multiProperties");

        for (IProperty property : this.getProperties(uniqueId)) {

            if (property == null) {
                continue;
            }

            if (property.isSingleProperty()) {
                singleProperties.append(property.getName(), property);

            } else {
                JsonData sub = new JsonData();
                for (IProperty propertyProperty : property.getProperties()) {
                    sub.append(propertyProperty.getName(), propertyProperty);
                }
                multiProperties.append(property.getName(), sub);
            }

        }

        jsonData.append("singleProperties", singleProperties);
        jsonData.append("multiProperties", multiProperties);
        jsonData.save();
    }

    @Override
    public void saveAll() throws PoloTypeUnsupportedActionException {
        if (PoloCloudAPI.getInstance().getType() != PoloType.MASTER) {
            throw new PoloTypeUnsupportedActionException("PoloInstances of type " + PoloCloudAPI.getInstance().getType() + " do not support saving Properties!");
        }

        for (UUID uuid : this.properties.keySet()) {
            this.save(uuid);
        }
    }

    @Override
    public void insertProperty(UUID uuid, Consumer<IProperty> property) {

        IProperty prop = new SimpleProperty();
        property.accept(prop);

        List<IProperty> properties = getProperties(uuid);
        properties.add(prop);
        this.properties.put(uuid, properties);

        PoloCloudAPI.getInstance().sendPacket(new PropertyInsertPacket(prop, uuid));
    }


    @Override
    public void deleteProperty(UUID uuid, String property) {
        List<IProperty> properties = getProperties(uuid);
        properties.removeIf(p -> p.getName().equalsIgnoreCase(property));
        this.properties.put(uuid, properties);

        PoloCloudAPI.getInstance().sendPacket(new PropertyDeletePacket(uuid, property));
    }

    public void setProperties(Map<UUID, List<IProperty>> properties) {
        this.properties = properties;
    }

    public Map<UUID, List<IProperty>> getProperties() {
        return properties;
    }
}
