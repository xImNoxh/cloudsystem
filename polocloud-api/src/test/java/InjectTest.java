import de.polocloud.api.inject.annotation.Inject;
import de.polocloud.api.inject.SimpleInjector;
import de.polocloud.api.inject.base.Injector;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.scheduler.base.SimpleScheduler;
import lombok.Getter;

public class InjectTest {

    public static void main(String[] args) {

        Injector injector = new SimpleInjector();
        
        injector.bind(Scheduler.class).annotatedWith("sc1").toInstance(new SimpleScheduler());
        injector.bind(Scheduler.class).annotatedWith("sc2").toInstance(null);

        injector.bind(String.class).annotatedWith("example_name").toInstance("Example Name");
        injector.bind(String.class).annotatedWith("example_version").toInstance("Alpha-1.0");

        ExampleObject exampleObject = injector.getInstance(ExampleObject.class);

        System.out.println("==============");
        System.out.println(exampleObject.getScheduler1());
        System.out.println(exampleObject.getScheduler2());
        System.out.println(exampleObject.getName());
        System.out.println(exampleObject.getVersion());
        System.out.println("==============");

    }


    @Getter
    public static class ExampleObject {

        private final Scheduler scheduler1;
        private final Scheduler scheduler2;
        private final String version;
        private final String name;

        @Inject
        public ExampleObject(@Inject("sc1") Scheduler scheduler1, @Inject("sc2") Scheduler scheduler2, @Inject("example_version") String version, @Inject("example_name") String name) {
            this.scheduler1 = scheduler1;
            this.scheduler2 = scheduler2;
            this.version = version;
            this.name = name;
        }
    }
}
