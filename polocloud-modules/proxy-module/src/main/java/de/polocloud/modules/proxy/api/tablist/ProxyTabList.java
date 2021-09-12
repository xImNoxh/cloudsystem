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
     * The ticks the tablist should update
     * (Set to -1 for no animation)
     */
    private long updateInterval;

    /**
     * The header lines (array for animations)
     */
    private String[] headers;

    /**
     * The footer lines (array for animation)
     */
    private String[] footers;

}
