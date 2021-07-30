package de.polocloud.signs.config;

import de.polocloud.api.config.IConfig;
import de.polocloud.signs.config.layout.SignLayouts;
import de.polocloud.signs.config.locations.SignLocationObject;
import de.polocloud.signs.config.messages.SignMessages;
import de.polocloud.signs.config.protection.SignProtection;

public class SignConfig implements IConfig {

    private boolean use = true;
    private boolean connectIfFull = false;
    private boolean canUseConnectIfFullPermission = true;
    private String connectIfFullPermission = "cloud.test";

    private SignMessages signMessages = new SignMessages();
    private SignLayouts signLayouts = new SignLayouts();
    private SignProtection signProtection = new SignProtection();
    private SignLocationObject locationConfig = new SignLocationObject();

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

    public boolean isConnectIfFull() {
        return connectIfFull;
    }

    public boolean isCanUseConnectIfFullPermission() {
        return canUseConnectIfFullPermission;
    }

    public String getConnectIfFullPermission() {
        return connectIfFullPermission;
    }

    public SignProtection getSignProtection() {
        return signProtection;
    }
}
