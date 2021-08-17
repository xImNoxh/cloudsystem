package de.polocloud.api.template;

import java.io.Serializable;

public enum TemplateType implements Serializable {

    /**
     * It's a proxy instance
     */
    PROXY,

    /**
     * It's a spigot instance
     */
    MINECRAFT;

    /**
     * The display name formatted
     * where the first letter is uppercase
     */
    public String getDisplayName() {
        return name().toUpperCase().charAt(0) + name().substring(1).toLowerCase();
    }

}
