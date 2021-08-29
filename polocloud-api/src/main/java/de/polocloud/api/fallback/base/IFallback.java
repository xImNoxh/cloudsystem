package de.polocloud.api.fallback.base;

import de.polocloud.api.template.base.ITemplate;

public interface IFallback {

    /**
     * The template for this fallback
     */
    ITemplate getTemplate();

    /**
     * The template name
     */
    String getTemplateName();

    /**
     * The permission to access it
     */
    String getFallbackPermission();

    /**
     * IF this fallback forces you to join
     */
    boolean isForcedJoin();

    /**
     * The priority of this fallback
     */
    int getPriority();


    default boolean hasPermission() {
        return getFallbackPermission() != null && !getFallbackPermission().trim().isEmpty();
    }
}
