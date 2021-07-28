package de.polocloud.signs.config.locations;

import com.google.common.collect.Lists;
import de.polocloud.signs.signs.ConfigSignLocation;

import java.util.List;

public class SignLocationObject {

    private List<ConfigSignLocation> locations = Lists.newArrayList();

    public List<ConfigSignLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<ConfigSignLocation> locations) {
        this.locations = locations;
    }
}
