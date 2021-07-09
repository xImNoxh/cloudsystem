package de.polocloud.api.guice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.SimpleProtocol;
import de.polocloud.api.util.Snowflake;

public class PoloAPIGuiceModule extends AbstractModule {

    //load from config/sql
    private int serverStartPort = 8869;
    private int protocolThreadSize = 8;

    private String clientHost = "127.0.0.1";
    private int clientPort = 8869;

    private Snowflake snowflake = new Snowflake();

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    protected void configure() {

        bind(Snowflake.class).toInstance(snowflake);

        bind(IProtocol.class).toInstance(new SimpleProtocol());

        bind(Gson.class).toInstance(gson);

        bind(int.class).annotatedWith(Names.named("setting_server_start_port")).toInstance(this.serverStartPort);
        bind(int.class).annotatedWith(Names.named("setting_protocol_threadSize")).toInstance(this.protocolThreadSize);

        bind(String.class).annotatedWith(Names.named("setting_client_host")).toInstance(this.clientHost);
        bind(int.class).annotatedWith(Names.named("setting_client_port")).toInstance(this.clientPort);
    }
}
