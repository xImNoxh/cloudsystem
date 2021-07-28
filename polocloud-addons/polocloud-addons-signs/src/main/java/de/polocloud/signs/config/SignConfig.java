package de.polocloud.signs.config;

import de.polocloud.api.config.IConfig;
import de.polocloud.signs.config.locations.SignLocationObject;

public class SignConfig implements IConfig {

    private boolean use = true;

    private SignLocationObject locationConfig = new SignLocationObject();


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
}
