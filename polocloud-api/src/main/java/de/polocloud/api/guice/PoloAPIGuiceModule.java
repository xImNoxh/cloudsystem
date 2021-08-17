package de.polocloud.api.guice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.SimpleProtocol;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.scheduler.SimpleScheduler;
import de.polocloud.api.util.PoloUtils;
import de.polocloud.api.util.Snowflake;

public class PoloAPIGuiceModule extends AbstractModule {

    //load from config
    private int serverStartPort = 8869;
    private int protocolThreadSize = 8;

    private final Snowflake snowflake = new Snowflake();

    @Override
    protected void configure() {

        bind(Snowflake.class).toInstance(snowflake);

        bind(IProtocol.class).toInstance(new SimpleProtocol());
        bind(Scheduler.class).toInstance(new SimpleScheduler());

        bind(Gson.class).toInstance(PoloUtils.GSON_INSTANCE);

        bind(int.class).annotatedWith(Names.named("setting_server_start_port")).toInstance(this.serverStartPort);
        bind(int.class).annotatedWith(Names.named("setting_protocol_threadSize")).toInstance(this.protocolThreadSize);
    }
}
