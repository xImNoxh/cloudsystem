import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.polocloud.api.network.protocol.IProtocol;
import de.polocloud.api.network.protocol.SimpleProtocol;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.scheduler.base.SimpleScheduler;

public class Comparison extends AbstractModule {


    public static void main(String[] args) {
        new Comparison();
    }



    public Comparison() {
        System.out.println("Google || =========================");
        long start = System.currentTimeMillis();
        Injector injector = Guice.createInjector(this);

        injector.getInstance(Scheduler.class).schedule(() -> System.out.println("Google-Scheduled Task!"));
        System.out.println("Google || " + (System.currentTimeMillis() - start) + "ms ==================");

        System.out.println("Polo || =========================");
        start = System.currentTimeMillis();

        de.polocloud.api.guice.own.Guice.bind(Scheduler.class).toInstance(new SimpleScheduler());
        de.polocloud.api.guice.own.Guice.getInstance(Scheduler.class).schedule(() -> System.out.println("Polo-Scheduled Task!"));
        System.out.println("Polo || " + (System.currentTimeMillis() - start) + "ms ==================");
    }

    @Override
    protected void configure() {
        bind(Scheduler.class).toInstance(new SimpleScheduler());
    }
}
