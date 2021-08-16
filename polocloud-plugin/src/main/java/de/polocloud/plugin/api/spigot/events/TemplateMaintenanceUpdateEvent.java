package de.polocloud.plugin.api.spigot.events;

import de.polocloud.api.template.ITemplate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TemplateMaintenanceUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private ITemplate template;

    public TemplateMaintenanceUpdateEvent(ITemplate template) {
        this.template = template;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ITemplate getTemplate() {
        return template;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

