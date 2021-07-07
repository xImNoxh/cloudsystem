package de.polocloud.api.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.SimpleProtocol;

public class PoloAPIGuiceModule extends AbstractModule {

    //load from config/sql
    private int serverStartPort = 8869;
    private int protocolThreadSize = 8;

    private String clientHost = "127.0.0.1";
    private int clientPort = 8869;

    @Override
    protected void configure() {

        bind(IProtocol.class).toInstance(new SimpleProtocol());

        bind(int.class).annotatedWith(Names.named("setting_server_start_port")).toInstance(this.serverStartPort);
        bind(int.class).annotatedWith(Names.named("setting_protocol_threadSize")).toInstance(this.protocolThreadSize);

        bind(String.class).annotatedWith(Names.named("setting_client_host")).toInstance(this.clientHost);
        bind(int.class).annotatedWith(Names.named("setting_client_port")).toInstance(this.clientPort);
    }
}
