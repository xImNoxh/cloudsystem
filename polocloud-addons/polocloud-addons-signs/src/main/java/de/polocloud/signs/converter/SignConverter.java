package de.polocloud.signs.converter;

import com.google.common.collect.Maps;
import de.polocloud.api.gameserver.IGameServer;

import java.util.Map;

public class SignConverter {

    private static Map<String, ConvertStep> converts = Maps.newConcurrentMap();

    public SignConverter() {
        converts.put("%SERVICE%", gameServer -> gameServer == null ? "null" : gameServer.getName());
        converts.put("%MAX_PLAYERS%", gameServer ->  gameServer == null ? "null" : gameServer.getTemplate().getMaxPlayers());
        converts.put("%ONLINE_PLAYERS%", gameServer ->  gameServer == null ? "null" : gameServer.getOnlinePlayers());
        converts.put("%MOTD%", gameServer ->  gameServer == null ? "null" :  gameServer.getTemplate().getMotd());
        converts.put("%GROUP%", gameServer ->  gameServer == null ? "null" :gameServer.getTemplate().getName());
    }

    public static String convertSignLayout(IGameServer gameServer, String line) {
        String output = line;
        for (String ids : converts.keySet()) {
            output = output.replaceAll(ids, converts.get(ids).execute(gameServer).toString());
        }
        return output;
    }

}
