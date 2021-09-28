package de.polocloud.api.util.system;

import de.polocloud.api.util.system.resources.IResourceConverter;
import de.polocloud.api.util.system.resources.IResourceProvider;
import de.polocloud.api.util.system.resources.impl.SimpleResourceConverter;
import de.polocloud.api.util.system.resources.impl.SimpleResourceProvider;

public class SystemManager {

    private final IResourceConverter resourceConverter;
    private final IResourceProvider resourceProvider;

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

    public String getSystemUserName(){
        return System.getProperty("user.name");
    }
}
