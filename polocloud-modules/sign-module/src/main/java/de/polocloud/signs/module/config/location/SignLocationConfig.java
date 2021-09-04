package de.polocloud.signs.module.config.location;

import de.polocloud.api.config.IConfig;
import de.polocloud.signs.sign.location.SignLocation;

import java.util.ArrayList;

public class SignLocationConfig implements IConfig {

    private ArrayList<SignLocation> locations = new ArrayList<>();

    public ArrayList<SignLocation> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<SignLocation> locations) {
        this.locations = locations;
    }

}
