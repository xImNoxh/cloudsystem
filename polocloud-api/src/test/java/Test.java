import com.google.gson.Gson;
import de.polocloud.api.guice.own.Guice;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.SimpleProtocol;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.scheduler.base.SimpleScheduler;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.api.util.Snowflake;
import de.polocloud.api.util.WrappedObject;

import java.util.UUID;

public class Test {


    public static void main(String[] args) {

        WrappedObject<UUID> wrappedObject = new WrappedObject<>(UUID.randomUUID());


        System.out.println(wrappedObject.unwrap(UUID.class));

    }
}
