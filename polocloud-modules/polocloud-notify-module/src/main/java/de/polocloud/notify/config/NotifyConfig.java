package de.polocloud.notify.config;

import de.polocloud.api.config.IConfig;
import de.polocloud.notify.config.messages.Messages;

public class NotifyConfig implements IConfig {

    private boolean use = true;
    private String permission = "cloud.test";

    private Messages messages = new Messages();

    public boolean isUse() {
        return use;
    }

    public String getPermission() {
        return permission;
    }
}
