package de.polocloud.bootstrap.template.fallback;

public class FallbackProperty {

    private final String templateName, fallbackPermission;
    private final boolean forcedJoin;
    private final int priority;

    public FallbackProperty(String templateName, String fallbackPermission, boolean forcedJoin, int priority) {
        this.templateName = templateName;
        this.fallbackPermission = fallbackPermission;
        this.forcedJoin = forcedJoin;
        this.priority = priority;
    }

    public String getTemplateName() {
        return templateName;
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
