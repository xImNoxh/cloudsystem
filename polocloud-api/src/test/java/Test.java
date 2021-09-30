import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import de.polocloud.api.util.session.ISession;
import de.polocloud.api.util.session.SimpleSession;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        ISession session = new SimpleSession();
        session.randomize();


        System.out.println(Arrays.toString(session.getBytes()));

        System.out.println(session.toString());

        byte[] decode = Base64.decode(session.getIdentification());
        System.out.println(Arrays.toString(decode));


    }
}
