import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import de.polocloud.api.gameserver.helper.GameServerVersion;
import de.polocloud.api.template.SimpleTemplate;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.api.util.session.ISession;
import de.polocloud.api.util.session.SimpleSession;

import java.util.Arrays;

public class Test {

    public static void main1(String[] args) {
        ISession session = new SimpleSession();
        session.randomize();


        System.out.println(Arrays.toString(session.getBytes()));

        System.out.println(session.toString());

        byte[] decode = Base64.decode(session.getIdentification());
        System.out.println(Arrays.toString(decode));


    }

}
