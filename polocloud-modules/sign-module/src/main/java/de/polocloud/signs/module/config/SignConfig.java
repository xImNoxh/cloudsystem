package de.polocloud.signs.module.config;

import de.polocloud.api.config.IConfig;
import de.polocloud.signs.module.config.messages.SignMessagesConfig;

public class SignConfig implements IConfig {

    private final SignMessagesConfig signMessages = new SignMessagesConfig();

    public SignMessagesConfig getSignMessages() {
        return signMessages;
    }

}
