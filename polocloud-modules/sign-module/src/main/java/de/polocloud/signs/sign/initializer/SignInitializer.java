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

        System.out.println("init");
        System.out.println("deleting cache");
        PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getLoadedSigns().clear();
        System.out.println("adding locations...");
        for (SignLocation location : PluginBootstrap.getInstance().getSignService().getCurrentGlobalConfig().getLocations()) {
            PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getLoadedSigns().add(loadSign(location));
        }
        System.out.println("added " + PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getLoadedSigns().size());
        System.out.println("loading for template");
        for (ITemplate iTemplate : PoloCloudAPI.getInstance().getTemplateManager().getTemplates().stream().filter(template -> template.getTemplateType().equals(TemplateType.MINECRAFT)).collect(Collectors.toList())) {
            System.out.println("template: " + iTemplate.getName());
            for (IGameServer gameServer : PoloCloudAPI.getInstance().getGameServerManager().getAllCached().stream().filter(server -> server.getTemplate().equals(iTemplate) &&
                !server.getName().equalsIgnoreCase(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getName())).collect(Collectors.toList())) {
                System.out.println("server: " + gameServer.getName());
                if(PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getSignByGameServer(gameServer) == null){
                    IGameServerSign gameServerSign = PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getFreeGameServerSign(gameServer);
                    if (gameServerSign != null) {
                        System.out.println("found sign: " + gameServerSign.getLocation().toString());
                        gameServerSign.setGameServer(gameServer);
                        gameServerSign.writeSign(false);
                    }
                }else{
                    System.out.println("has already: " + gameServer.getName());
                }
            }
        }

        System.out.println((int) PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getLoadedSigns().stream().filter(sign -> sign.getGameServer() == null).count());

        for (IGameServerSign loadedSign : PluginBootstrap.getInstance().getSignService().getGameServerSignManager().getLoadedSigns()) {
            System.out.println(loadedSign.getGameServer() == null ? "null " + loadedSign.getLocation() : loadedSign.getGameServer().getName() + " " + loadedSign.getLocation());
        }
    }

    public IGameServerSign loadSign(SignLocation signLocation){
        return new SimpleGameServerSign(signLocation);
    }
}
