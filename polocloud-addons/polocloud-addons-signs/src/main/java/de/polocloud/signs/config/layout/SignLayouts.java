package de.polocloud.signs.config.layout;

import com.google.common.collect.Maps;
import de.polocloud.signs.enumeration.SignState;

import java.util.Map;

public class SignLayouts {

    private Map<SignState, Layout[]> signLayouts = Maps.newConcurrentMap();

    public SignLayouts() {

        signLayouts.put(SignState.LOADING, new Layout[]{
            new Layout("","Lading","...")
        });

        signLayouts.put(SignState.FULL, new Layout[]{
            new Layout("%SERVICE%","§cFull","%ONLINE_PLAYERS%", "%MOTD%")
        });

        signLayouts.put(SignState.ONLINE, new Layout[]{
            new Layout("%SERVICE%","§aOnline","%ONLINE_PLAYERS% / %MAXPLAYERS%", "%MOTD%")
        });

        signLayouts.put(SignState.PLAYERS, new Layout[]{
            new Layout("%SERVICE%","§a§lJoin","%ONLINE_PLAYERS%  / %MAXPLAYERS%", "%MOTD%")
        });

        signLayouts.put(SignState.MAINTENANCE, new Layout[]{
            new Layout("","%TEMPLATE%","§cMaintenance", "")
        });
    }

    public Map<SignState, Layout[]> getSignLayouts() {
        return signLayouts;
    }
}
