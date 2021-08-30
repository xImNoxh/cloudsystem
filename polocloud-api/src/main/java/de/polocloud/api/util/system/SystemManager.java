package de.polocloud.api.util.system;

import de.polocloud.api.util.system.ressources.IResourceConverter;
import de.polocloud.api.util.system.ressources.IResourceProvider;
import de.polocloud.api.util.system.ressources.impl.SimpleResourceConverter;
import de.polocloud.api.util.system.ressources.impl.SimpleResourceProvider;

public class SystemManager {

    private IResourceConverter resourceConverter;
    private IResourceProvider resourceProvider;

    public SystemManager() {
        resourceConverter = new SimpleResourceConverter();
        resourceProvider = new SimpleResourceProvider();
    }

    public IResourceConverter getResourceConverter() {
        return resourceConverter;
    }

    public IResourceProvider getResourceProvider() {
        return resourceProvider;
    }
}
