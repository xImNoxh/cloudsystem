package de.polocloud.example.config;

import de.polocloud.api.config.IConfig;

import java.util.UUID;

public class ExampleConfig implements IConfig {

    private final boolean enabled;
    private final String yourString;
    private final UUID yourUUID;

    public ExampleConfig(boolean enabled, String yourString, UUID yourUUID) {
        this.enabled = enabled;
        this.yourString = yourString;
        this.yourUUID = yourUUID;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getYourString() {
        return yourString;
    }

    public UUID getYourUUID() {
        return yourUUID;
    }

    @Override
    public String toString() {
        return "ExampleConfig{" +
            "enabled=" + enabled +
            ", yourString='" + yourString + '\'' +
            ", yourUUID=" + yourUUID +
            '}';
    }
}
