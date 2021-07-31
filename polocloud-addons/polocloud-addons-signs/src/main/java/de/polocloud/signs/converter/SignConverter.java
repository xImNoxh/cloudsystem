package de.polocloud.signs.converter;

import com.google.common.collect.Maps;
import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.signs.signs.IGameServerSign;

import java.util.Map;

public class SignConverter {

    private static Map<String, ConvertStep> converts = Maps.newConcurrentMap();

    public SignConverter() {
        converts.put("%SERVICE%", signService -> signService.getGameServer() == null ? "null" : signService.getGameServer().getName());
        converts.put("%MAX_PLAYERS%", signService ->  signService.getGameServer() == null ? "null" : signService.getGameServer().getMaxPlayers());
        converts.put("%ONLINE_PLAYERS%", signService ->  signService.getGameServer() == null ? "null" : signService.getGameServer().getOnlinePlayers());
        converts.put("%MOTD%", signService ->  signService.getGameServer() == null ? "null" :  signService.getGameServer().getMotd());
        converts.put("%GROUP%", signService ->  signService.getGameServer() == null ? "null" : signService.getTemplate().getName());
        converts.put("%TEMPLATE%", signService ->  signService.getTemplate().getName());
    }

    public static String convertSignLayout(IGameServerSign gameServer, String line) {
        String output = line;
        for (String ids : converts.keySet()) {
            output = output.replaceAll(ids, converts.get(ids).execute(gameServer).toString());
        }
        return output;
    }

}
