import de.polocloud.api.guice.own.GuiceObject;
import de.polocloud.api.guice.own.Inject;
import de.polocloud.api.network.protocol.IProtocol;

public class TestObject extends GuiceObject {

    @Inject
    private IProtocol protocol;

    private final String name;

    public TestObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public IProtocol getProtocol() {
        return protocol;
    }
}
