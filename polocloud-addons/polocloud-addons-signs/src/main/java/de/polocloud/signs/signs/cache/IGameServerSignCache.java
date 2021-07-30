package de.polocloud.signs.signs.cache;

import de.polocloud.signs.signs.IGameServerSign;
import org.bukkit.Location;

import java.util.ArrayList;

public class IGameServerSignCache extends ArrayList<IGameServerSign> {

    public boolean alreadySign(Location location){
        return stream().anyMatch(key -> key.getLocation().equals(location));
    }

}
