package de.polocloud.modules.serverselector.cloudside.api;

import de.polocloud.api.config.JsonData;
import de.polocloud.modules.serverselector.api.SignAPI;
import de.polocloud.modules.serverselector.api.config.SignConfiguration;
import de.polocloud.modules.serverselector.api.elements.CloudSign;
import de.polocloud.modules.serverselector.api.subapi.GlobalSignAPI;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
public class ModuleGlobalAPI implements GlobalSignAPI {

    /**
     * The files to manage
     */
    private final File configFile, signsFile;

    /**
     * The config
     */
    private SignConfiguration config;

    /**
     * The currently cached {@link CloudSign}s
     */
    private List<CloudSign> currentCloudSigns;

    public ModuleGlobalAPI(File configFile, File signsFile) {
        this.configFile = configFile;
        this.signsFile = signsFile;
        this.config = new SignConfiguration();
        this.currentCloudSigns = new ArrayList<>();
    }

    @Override
    public void addSign(CloudSign sign) {

    }

    @Override
    public void removeSign(CloudSign sign) {

    }

    @Override
    public CloudSign getCloudSign(int x, int y, int z, String world) {

        return this.currentCloudSigns.stream().filter(cloudSign -> cloudSign.getX() == x && cloudSign.getY() == y && cloudSign.getZ() == z && world.equalsIgnoreCase(cloudSign.getWorld())).findFirst().orElse(null);
    }

    @Override
    public void load() {
        JsonData data = new JsonData(configFile);

        SignConfiguration signConfiguration;
        if (data.isEmpty()) {
            signConfiguration = new SignConfiguration();
            data.append(signConfiguration);
            data.save();
        } else {
            signConfiguration = data.getAs(SignConfiguration.class);
        }
        this.config = signConfiguration;

        //Loading signs
        JsonData config = new JsonData(this.signsFile);

        if (config.isEmpty()) {
            config.save(this.signsFile);
        } else {
            this.currentCloudSigns = new LinkedList<>(config.keySet(CloudSign.class));
        }

    }

    @Override
    public void save() {
        JsonData signs = new JsonData();
        signs.clear();
        for (CloudSign cloudSign : this.currentCloudSigns) {
            signs.append(cloudSign.getUuid().toString(), cloudSign);
        }
        signs.save(this.signsFile);


        JsonData config = new JsonData();

        config.append(this.config);
        config.save(configFile);
    }
}
