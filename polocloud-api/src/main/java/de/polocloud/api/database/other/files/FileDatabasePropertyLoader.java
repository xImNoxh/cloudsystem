package de.polocloud.api.database.other.files;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.config.JsonData;
import de.polocloud.api.database.other.IDatabasePropertyLoader;
import de.polocloud.api.network.packets.property.PropertyDeletePacket;
import de.polocloud.api.network.packets.property.PropertyInsertPacket;
import de.polocloud.api.property.IProperty;
import de.polocloud.api.property.def.SimpleProperty;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class FileDatabasePropertyLoader implements IDatabasePropertyLoader<FileDatabase> {


    private Map<UUID, List<IProperty>> properties;

    public FileDatabasePropertyLoader() {
        this.properties = new ConcurrentHashMap<>();
    }

    @Override
    public Map<UUID, List<IProperty>> loadProperties() {
        Map<UUID, List<IProperty>> map = new HashMap<>();
        File directory = FileConstants.MASTER_PLAYER_PROPERTIES;

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File[] files = directory.listFiles();

        //Some os mark empty dirs as null
        if (files == null) {
            return map;
        }

        int downloaded = 0;
        int max = files.length;


        ProgressBar pb = new ProgressBar("Loading " + max + " Properties", 100, 1000, System.err, ProgressBarStyle.COLORFUL_UNICODE_BLOCK, "", 1, false, null, ChronoUnit.SECONDS, 0L, Duration.ZERO);

        long start = System.currentTimeMillis();
        for (File file : files) {
            List<IProperty> properties = new ArrayList<>();

            UUID uniqueId = UUID.fromString(file.getName().split("\\.")[0]);

            JsonData jsonData = new JsonData(file);
            JsonData singleProperties = jsonData.fallback(new JsonData()).getData("singleProperties");
            JsonData multiProperties = jsonData.fallback(new JsonData()).getData("multiProperties");

            for (String key : singleProperties.keySet()) {
                JsonData sub = singleProperties.getData(key);
                IProperty property = new SimpleProperty(sub.getString("name"), new ArrayList<>(), sub.getElement("value"));
                properties.add(property);
            }

            properties.addAll(this.getProperty(multiProperties));

            downloaded += 1;
            map.put(uniqueId, properties);
            pb.stepTo((long) ((downloaded * 100L) / (max * 1.0)));
        }
        pb.setExtraMessage("[" + (System.currentTimeMillis() - start) + "ms]");
        pb.close();

        this.properties = map;
        return map;
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
    public void save(UUID uniqueId) {
        File directory = FileConstants.MASTER_PLAYER_PROPERTIES;

        JsonData jsonData = new JsonData(new File(directory, uniqueId + ".json"));
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
    public void saveAll(){
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

        if (PoloCloudAPI.getInstance().getType().isCloud()) {
            save(uuid);
            PoloCloudAPI.getInstance().updateCache();
        } else {
            PoloCloudAPI.getInstance().sendPacket(new PropertyInsertPacket(prop, uuid));
        }
    }


    @Override
    public void deleteProperty(UUID uuid, String property) {
        List<IProperty> properties = getProperties(uuid);
        properties.removeIf(p -> p.getName().equalsIgnoreCase(property));
        this.properties.put(uuid, properties);
        if (PoloCloudAPI.getInstance().getType().isCloud()) {
            save(uuid);
            PoloCloudAPI.getInstance().updateCache();
        } else {
            PoloCloudAPI.getInstance().sendPacket(new PropertyDeletePacket(uuid, property));
        }
    }

}
