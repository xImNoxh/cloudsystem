package de.polocloud.api.database.other.mongodb;

import de.polocloud.api.database.other.IDatabasePropertyLoader;
import de.polocloud.api.property.IProperty;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class MongoDBDatabasePropertyLoader implements IDatabasePropertyLoader<MongoDBDatabase> {

    @Override
    public Map<UUID, List<IProperty>> loadProperties() {
        return null;
    }

    @Override
    public List<IProperty> getProperties(UUID uniqueId) {
        return null;
    }

    @Override
    public void saveAll() {

    }

    @Override
    public void save(UUID uniqueId) {

    }

    @Override
    public IProperty getProperty(UUID uniqueId, String name) {
        return null;
    }

    @Override
    public void insertProperty(UUID uuid, Consumer<IProperty> property) {

    }

    @Override
    public void deleteProperty(UUID uuid, String property) {

    }
}
