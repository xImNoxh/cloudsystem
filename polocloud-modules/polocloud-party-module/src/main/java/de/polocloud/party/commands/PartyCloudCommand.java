package de.polocloud.party.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.party.PartyService;
import de.polocloud.party.objects.Party;
import de.polocloud.party.objects.PartyUser;

@CloudCommand.Info(name = "party", commandType = CommandType.INGAME, description = "", aliases = "")
public class PartyCloudCommand extends CloudCommand {

    @Override
    public void execute(ICommandExecutor sender, String[] args) {

        ICloudPlayer player = (ICloudPlayer) sender;
        PartyService partyService = PartyService.getInstance();
        PartyUser user = partyService.getPartyUser(player.getUUID());

        if(args.length == 3 && args[1].equalsIgnoreCase("invite")){

            PartyUser partyUser = partyService.getPartyUser(args[2]);

            if(partyUser == null){
                player.sendMessage("nix dieser spieler xd");
                return;
            }

            if(partyService.isParty(partyUser.getUuid())){
                player.sendMessage("bereits party");
                return;
            }

            Party party = partyService.isParty(player.getUUID()) ? user.getCurrentParty() : partyService.newParty(user);
            partyUser.getRequests().add(party);

            player.sendMessage("du hast eingeladen " + partyUser.getCloudPlayer().getName());
            partyUser.getCloudPlayer().sendMessage("du wurdest eingeladen von " + player.getName());
            return;
        }


    }
}
