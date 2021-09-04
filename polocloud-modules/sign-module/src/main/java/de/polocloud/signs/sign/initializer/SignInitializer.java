package de.polocloud.signs.sign.initializer;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.template.base.ITemplate;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.signs.bootstraps.PluginBootstrap;
import de.polocloud.signs.sign.base.IGameServerSign;
import de.polocloud.signs.sign.base.impl.SimpleGameServerSign;
import de.polocloud.signs.sign.location.SignLocation;

import java.util.stream.Collectors;

public class SignInitializer {

    public void initSigns() {
        for (ITemplate iTemplate : PoloCloudAPI.getInstance().getTemplateManager().getTemplates().stream().filter(template -> template.getTemplateType().equals(TemplateType.MINECRAFT)).collect(Collectors.toList())) {
            for (IGameServer gameServer : PoloCloudAPI.getInstance().getGameServerManager().getAllCached().stream().filter(server -> server.getTemplate().equals(iTemplate) &&
                !server.getName().equalsIgnoreCase(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getName())).collect(Collectors.toList())) {
                IGameServerSign gameServerSign = PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getFreeGameServerSign(gameServer);
                if (gameServerSign != null) {
                    gameServerSign.setGameServer(gameServer);
                    gameServerSign.writeSign(false);
                }
            }
        }
    }

    public IGameServerSign loadSign(SignLocation signLocation){
        return new SimpleGameServerSign(signLocation);
    }
}
