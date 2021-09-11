package de.polocloud.modules.proxy.api.tablist;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class ProxyTabList {

    /**
     * If this Tablist is enabled
     */
    private boolean enabled;

    /**
     * The header
     */
    private String header;

    /**
     * The footer
     */
    private String footer;

}
