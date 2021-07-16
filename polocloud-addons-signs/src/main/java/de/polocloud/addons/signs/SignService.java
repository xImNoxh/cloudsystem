package de.polocloud.addons.signs;

import de.polocloud.addons.signs.cache.SignCache;
import de.polocloud.addons.signs.executes.SignAddExecute;
import de.polocloud.addons.signs.executes.SignExecute;
import de.polocloud.addons.signs.executes.SignRemoveExecute;
import de.polocloud.addons.signs.executes.loading.SignAutoLoading;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.api.template.ITemplate;
import de.polocloud.api.template.TemplateType;
import de.polocloud.plugin.api.CloudExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SignService {

    private static SignService instance;
    private SignCache cache;

    private SignExecute addSign;
    private SignExecute removeSign;

    public SignService() {

        instance = this;
        this.cache = new SignCache();

        this.removeSign = new SignRemoveExecute(this);
        this.addSign = new SignAddExecute(this);


        //testing


        ITemplate template = null;
        try {
            template = CloudExecutor.getInstance().getGameServerManager().getGameServerByName("Lobby-1").get().getTemplate();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println(template != null);
        System.out.println(template.getName());

        Location location = new Location(Bukkit.getWorld("world"),-1269,5,-390);

        if(!location.getChunk().isLoaded()) location.getChunk().load();

        for(int i = 0; i < 3; i++){
            //addSign(iTemplate, location.clone().add(-i,0,0));
            location.clone().add(-i,0,0).getBlock().setType(Material.STONE);
        }

        //new SignAutoLoading(this, iTemplate);
    }

    public void addSign(ITemplate template, Location location){
        cache.add(new Sign(template, location));
    }

    public static SignService getInstance() {
        return instance;
    }

    public SignCache getCache() {
        return cache;
    }

    public List<Sign> getSignsByTemplate(ITemplate template) {
        return cache.stream().filter(key -> key.getTemplate().equals(template)).collect(Collectors.toList());
    }

    public Sign getSignByGameServer(IGameServer gameServer){
        return cache.stream().filter(key -> key.getGameServer().equals(gameServer)).findAny().orElse(null);
    }

    public Sign getNextFreeSignByTemplate(ITemplate template){
        return getSignsByTemplate(template).stream().filter(key -> key.getGameServer() == null).findAny().orElse(null);
    }

    public SignExecute getAddSign() {
        return addSign;
    }

    public SignExecute getRemoveSign() {
        return removeSign;
    }
}
