package de.polocloud.wrapper.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class WrapperGuiceModule extends AbstractModule {

    private String masterHost;
    private int masterPort;

    public WrapperGuiceModule(String masterHost, int masterPort) {
        this.masterHost = masterHost;
        this.masterPort = masterPort;
    }

    @Override
    protected void configure() {
        bind(String.class).annotatedWith(Names.named("setting_client_host")).toInstance(this.masterHost);
        bind(int.class).annotatedWith(Names.named("setting_client_port")).toInstance(this.masterPort);
    }
}
