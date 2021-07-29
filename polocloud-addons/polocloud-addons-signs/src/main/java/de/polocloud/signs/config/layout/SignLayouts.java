package de.polocloud.signs.config.layout;

import com.google.common.collect.Maps;
import de.polocloud.signs.enumeration.SignState;

import java.util.Map;

public class SignLayouts {

    private Map<SignState, Layout[]> signLayouts = Maps.newConcurrentMap();

    public SignLayouts() {

        signLayouts.put(SignState.LOADING, new Layout[]{
            new Layout(new BlockLayout(159,9),"---","Loading","...", "---")
        });

        signLayouts.put(SignState.FULL, new Layout[]{
            new Layout(new BlockLayout(159,4),"%SERVICE%","§cFull","%ONLINE_PLAYERS%", "%MOTD%")
        });

        signLayouts.put(SignState.ONLINE, new Layout[]{
            new Layout(new BlockLayout(159,5),"%SERVICE%","§aOnline","%ONLINE_PLAYERS% / %MAX_PLAYERS%", "%MOTD%")
        });

        signLayouts.put(SignState.PLAYERS, new Layout[]{
            new Layout(new BlockLayout(159,13),"%SERVICE%","§a§lJoin","%ONLINE_PLAYERS%  / %MAX_PLAYERS%", "%MOTD%")
        });

        signLayouts.put(SignState.MAINTENANCE, new Layout[]{
            new Layout(new BlockLayout(172,0)," ","%TEMPLATE%","§cMaintenance", " ")
        });
    }

    public Map<SignState, Layout[]> getSignLayouts() {
        return signLayouts;
    }
}
