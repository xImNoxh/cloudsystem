package de.polocloud.signs.config.locations;

import com.google.common.collect.Lists;
import de.polocloud.signs.config.utils.ConfigLocation;

import java.util.List;

public class LocationConfig {

    private List<ConfigLocation> locations = Lists.newArrayList();

    public List<ConfigLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<ConfigLocation> locations) {
        this.locations = locations;
    }
}
