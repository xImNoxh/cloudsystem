package de.polocloud.api.fallback.base;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.template.base.ITemplate;

public class SimpleFallback implements IFallback{

    private final String templateName, fallbackPermission;
    private final boolean forcedJoin;
    private final int priority;

    public SimpleFallback(String templateName, String fallbackPermission, boolean forcedJoin, int priority) {
        this.templateName = templateName;
        this.fallbackPermission = fallbackPermission;
        this.forcedJoin = forcedJoin;
        this.priority = priority;
    }

    public String getTemplateName() {
        return templateName;
    }

    @Override
    public ITemplate getTemplate() {
        return PoloCloudAPI.getInstance().getTemplateManager().getTemplate(this.templateName);
    }

    public String getFallbackPermission() {
        return fallbackPermission;
    }

    public boolean isForcedJoin() {
        return forcedJoin;
    }

    public int getPriority() {
        return priority;
    }
}
