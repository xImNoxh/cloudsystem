package de.polocloud.api.fallback.base;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.util.PoloUtils;

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
        return PoloUtils.sneakyThrows(() -> PoloCloudAPI.getInstance().getTemplateService().getTemplateByName(this.templateName).get());
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
