package de.polocloud.party.objects;

import com.google.common.collect.Lists;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.bootstrap.Master;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PartyUser {

    private UUID uuid;
    private ICloudPlayer cloudPlayer;

    private Party currentParty;
    private List<Party> requests;

    public PartyUser(UUID uuid) {
        this.uuid = uuid;

        try {
            cloudPlayer = Master.getInstance().getCloudPlayerManager().getOnlinePlayer(uuid).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        requests = Lists.newArrayList();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ICloudPlayer getCloudPlayer() {
        return cloudPlayer;
    }

    public void setCloudPlayer(ICloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
    }

    public Party getCurrentParty() {
        return currentParty;
    }

    public void setCurrentParty(Party currentParty) {
        this.currentParty = currentParty;
    }

    public List<Party> getRequests() {
        return requests;
    }

    public void setRequests(List<Party> requests) {
        this.requests = requests;
    }
}
