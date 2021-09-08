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

    /**
     *  Clears the current {@link IGameServerSign} cache,
     *  loads the {@link IGameServerSign} signs new
     *  and searches for the signs {@link ITemplate template} a {@link IGameServer gameserver}
     */
    public void initSigns() {
        PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getLoadedSigns().clear();
        for (SignLocation location : PluginBootstrap.getInstance().getSignService().getCurrentGlobalConfig().getLocations()) {
            PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getLoadedSigns().add(loadSign(location));
        }
        for (ITemplate iTemplate : PoloCloudAPI.getInstance().getTemplateManager().getTemplates().stream().filter(template -> template.getTemplateType().equals(TemplateType.MINECRAFT)).collect(Collectors.toList())) {
            for (IGameServer gameServer : PoloCloudAPI.getInstance().getGameServerManager().getAllCached().stream().filter(server -> server.getTemplate().equals(iTemplate) &&
                !server.getName().equalsIgnoreCase(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getName())).collect(Collectors.toList())) {
                if(PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getSignByGameServer(gameServer) == null){
                    IGameServerSign gameServerSign = PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getFreeGameServerSign(gameServer);
                    if (gameServerSign != null) {
                        gameServerSign.setGameServer(gameServer);
                        gameServerSign.writeSign(false);
                    }
                }
            }
        }
    }

    /**
     *  Creating a new instance of the {@link IGameServerSign} (using implemented class {@link SimpleGameServerSign})
     * @param signLocation for creating the {@link IGameServerSign}
     * @return 's the {@link IGameServerSign}
     */
    public IGameServerSign loadSign(SignLocation signLocation){
        return new SimpleGameServerSign(signLocation);
    }
}
