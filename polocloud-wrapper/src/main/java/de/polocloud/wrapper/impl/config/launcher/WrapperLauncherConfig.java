package de.polocloud.wrapper.impl.config.launcher;

import de.polocloud.api.APIVersion;
import de.polocloud.api.config.IConfig;
import de.polocloud.client.PoloCloudClient;

public class WrapperLauncherConfig implements IConfig {

    private String version = PoloCloudClient.class.getAnnotation(APIVersion .class) == null ? "N/A" : PoloCloudClient .class.getAnnotation(APIVersion.class).version();

    private boolean useUpdater = true, forceUpdate = true;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isUseUpdater() {
        return useUpdater;
    }

    public void setUseUpdater(boolean useUpdater) {
        this.useUpdater = useUpdater;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
}
