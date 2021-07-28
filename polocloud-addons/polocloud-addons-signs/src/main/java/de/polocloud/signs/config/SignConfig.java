package de.polocloud.signs.config;

import de.polocloud.api.config.IConfig;
import de.polocloud.signs.config.layout.SignLayouts;
import de.polocloud.signs.config.locations.SignLocationObject;
import de.polocloud.signs.config.messages.SignMessages;

public class SignConfig implements IConfig {

    private boolean use = true;

    private SignLocationObject locationConfig = new SignLocationObject();
    private SignMessages signMessages = new SignMessages();
    private SignLayouts signLayouts = new SignLayouts();

    public SignLayouts getSignLayouts() {
        return signLayouts;
    }

    public SignLocationObject getLocationConfig() {
        return locationConfig;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public void setLocationConfig(SignLocationObject locationConfig) {
        this.locationConfig = locationConfig;
    }

    public SignMessages getSignMessages() {
        return signMessages;
    }
}
