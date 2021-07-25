package de.polocloud.party.objects;

import com.google.common.collect.Lists;

import java.util.List;

public class Party {

    private PartyUser leader;

    private List<PartyUser> moderators;
    private List<PartyUser> members;

    public Party(PartyUser leader) {
        this.leader = leader;

        this.moderators = Lists.newArrayList();
        this.members = Lists.newArrayList();

        members.add(leader);
    }

    public List<PartyUser> getModerators() {
        return moderators;
    }

    public void setModerators(List<PartyUser> moderators) {
        this.moderators = moderators;
    }

    public List<PartyUser> getMembers() {
        return members;
    }

    public void setMembers(List<PartyUser> members) {
        this.members = members;
    }

    public PartyUser getLeader() {
        return leader;
    }

    public void setLeader(PartyUser leader) {
        this.leader = leader;
    }
}
