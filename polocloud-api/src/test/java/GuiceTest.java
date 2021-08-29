import com.google.gson.Gson;
import de.polocloud.api.guice.own.Guice;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.SimpleProtocol;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.scheduler.base.SimpleScheduler;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.api.util.Snowflake;

public class GuiceTest {


    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        Guice.bind(Snowflake.class).toInstance(new Snowflake());

        Guice.bind(IProtocol.class).toInstance(new SimpleProtocol());
        Guice.bind(Scheduler.class).toInstance(new SimpleScheduler());

        Guice.bind(Gson.class).toInstance(PoloHelper.GSON_INSTANCE);

        Guice.bind(int.class).annotatedWith("setting_server_start_port").toInstance(25565);
        Guice.bind(int.class).annotatedWith("setting_protocol_threadSize").toInstance(256);

        System.out.println(Guice.getInstance(Snowflake.class).nextId());
        System.out.println((System.currentTimeMillis() - start) + "Ms took");


        TestObject testObject = new TestObject("test");
        System.out.println(testObject.getProtocol());

    }
}
