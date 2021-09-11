import de.polocloud.api.util.WrappedObject;

import java.util.UUID;

public class Test {


    public static void main(String[] args) {

        WrappedObject<UUID> wrappedObject = new WrappedObject<>(UUID.randomUUID());

        System.out.println(wrappedObject.unwrap(UUID.class));
    }
}
