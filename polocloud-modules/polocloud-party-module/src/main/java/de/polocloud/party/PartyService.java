package de.polocloud.party;

import de.polocloud.party.cache.PartyUserCache;
import de.polocloud.party.objects.Party;
import de.polocloud.party.objects.PartyUser;

import java.util.UUID;

public class PartyService {

    private static PartyService instance;

    private PartyUserCache cache;

    public PartyService() {
        instance = this;

        cache = new PartyUserCache();
    }

    public PartyUserCache getCache() {
        return cache;
    }

    public static PartyService getInstance() {
        return instance;
    }

    public Party newParty(PartyUser partyUser){
        partyUser.setCurrentParty(new Party(partyUser));
        return partyUser.getCurrentParty();
    }

    public boolean isParty(UUID uuid) {
        return getPartyUser(uuid).getCurrentParty() != null;
    }

    public PartyUser getPartyUser(UUID uuid){
        return cache.stream().filter(key -> key.getUuid().equals(uuid)).findAny().get();
    }

    public PartyUser getPartyUser(String name){
        return cache.stream().filter(key -> key.getCloudPlayer().getName().equals(name)).findAny().get();
    }

}
