package de.polocloud.signs.module.config.layout;

import de.polocloud.api.config.IConfig;
import de.polocloud.signs.sign.enumeration.SignState;
import de.polocloud.signs.sign.layout.BlockLayout;
import de.polocloud.signs.sign.layout.Layout;

import java.util.HashMap;

public class SignLayoutConfig implements IConfig {

    private final HashMap<SignState, Layout[]> signLayouts = new HashMap<>();

    public SignLayoutConfig() {

        signLayouts.put(SignState.LOADING, new Layout[]{
            new Layout(new BlockLayout(159,9),"---","Loading",".", "---"),
            new Layout(new BlockLayout(159,9),"---","Loading","..", "---"),
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

    public HashMap<SignState, Layout[]> getSignLayouts() {
        return signLayouts;
    }

}
