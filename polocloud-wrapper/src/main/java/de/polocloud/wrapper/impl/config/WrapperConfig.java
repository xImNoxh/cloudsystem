package de.polocloud.wrapper.impl.config;

import de.polocloud.api.config.IConfig;
import de.polocloud.api.util.Snowflake;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WrapperConfig implements IConfig {

    private String loginKey;
    private String masterAddress;
    private String wrapperName;
    private long memory;
    private final long snowflake;
    private int maxSimultaneouslyStartingServices;
    private boolean logServerOutput;
    private boolean addCancelledServicesToQueue;

    private String apiVersion;

    public WrapperConfig() {
        this.apiVersion = "-1";
        this.wrapperName = "Wrapper-1";
        this.memory = 5000;
        this.addCancelledServicesToQueue = true;
        this.snowflake = Snowflake.getInstance().nextId();
        this.maxSimultaneouslyStartingServices = 3;
        this.masterAddress = "127.0.0.1:8869";
        this.loginKey = "default";
        this.logServerOutput = false;
    }

}
