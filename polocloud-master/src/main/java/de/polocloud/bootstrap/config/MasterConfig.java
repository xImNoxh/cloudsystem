package de.polocloud.bootstrap.config;

import de.polocloud.api.config.IConfig;
import de.polocloud.bootstrap.config.database.DatabaseSupport;
import de.polocloud.bootstrap.config.messages.Messages;
import de.polocloud.bootstrap.config.properties.Properties;

public class MasterConfig implements IConfig {

    private Properties properties = new Properties();
    private Messages messages = new Messages();
    private DatabaseSupport databaseSupport = new DatabaseSupport();

    public DatabaseSupport getDatabaseSupport() {
        return databaseSupport;
    }

    public Properties getProperties() {
        return properties;
    }

    public Messages getMessages() {
        return messages;
    }

}
