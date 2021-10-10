package de.polocloud.api.util.system;

import de.polocloud.api.util.system.resources.IResourceConverter;
import de.polocloud.api.util.system.resources.IResourceProvider;
import de.polocloud.api.util.system.resources.ISystemManagement;
import de.polocloud.api.util.system.resources.impl.SimpleResourceConverter;
import de.polocloud.api.util.system.resources.impl.SimpleResourceProvider;
import de.polocloud.api.util.system.resources.impl.SimpleSystemManagement;

public class SystemManager {

    private final IResourceConverter resourceConverter;
    private final IResourceProvider resourceProvider;
    private final ISystemManagement systemManagement;

    public SystemManager() {
        resourceConverter = new SimpleResourceConverter();
        resourceProvider = new SimpleResourceProvider();
        systemManagement = new SimpleSystemManagement();
    }

    public IResourceConverter getResourceConverter() {
        return resourceConverter;
    }

    public IResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    public ISystemManagement getSystemManagement() {
        return systemManagement;
    }

    public String getSystemUserName(){
        return System.getProperty("user.name");
    }
}
