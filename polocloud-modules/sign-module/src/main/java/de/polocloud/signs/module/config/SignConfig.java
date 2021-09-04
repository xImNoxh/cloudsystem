package de.polocloud.signs.module.config;

import de.polocloud.api.config.IConfig;
import de.polocloud.signs.module.config.messages.SignMessagesConfig;

public class SignConfig implements IConfig {

    private boolean useModule = true;
    private boolean connectIfFull = false;
    private boolean canUseConnectIfFullPermission = true;
    private String connectIfFullPermission = "cloud.server.full.connect";

    private SignMessagesConfig signMessages = new SignMessagesConfig();

    public boolean isUseModule() {
        return useModule;
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

    public SignMessagesConfig getSignMessages() {
        return signMessages;
    }

}
