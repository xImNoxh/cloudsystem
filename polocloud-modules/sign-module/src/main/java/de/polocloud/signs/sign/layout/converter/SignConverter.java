package de.polocloud.signs.sign.layout.converter;

import com.google.common.collect.Maps;
import de.polocloud.signs.sign.base.IGameServerSign;

import java.util.Map;

public class SignConverter {

    /**
     * List for containing all available placeholders
     */
    private static final Map<String, ConvertStep> converts = Maps.newConcurrentMap();

    /**
     * Initializes the placeholders
     */
    public SignConverter() {
        converts.put("%SERVICE%", signService -> signService.getGameServer() == null ? "N/A" : signService.getGameServer().getName());
        converts.put("%MAX_PLAYERS%", signService ->  signService.getGameServer() == null ? "N/A" : signService.getGameServer().getMaxPlayers());
        converts.put("%ONLINE_PLAYERS%", signService ->  signService.getGameServer() == null ? "N/A" : signService.getGameServer().getOnlinePlayers());
        converts.put("%MOTD%", signService ->  signService.getGameServer() == null ? "N/A" :  signService.getGameServer().getMotd());
        converts.put("%GROUP%", signService ->  signService.getGameServer() == null ? "N/A" : signService.getTemplate().getName());
        converts.put("%TEMPLATE%", signService ->  signService.getTemplate().getName());
    }

    /**
     *  Converts all placeholders in a line with the values
     *  from the {@link IGameServerSign}
     * @param sign for replacing the placeholders with
     * @param line unconverted {@link String line}
     * @return the converted {@link String}
     */
    public static String convertSignLayout(IGameServerSign sign, String line) {
        String output = line;
        for (String ids : converts.keySet()) {
            if(output.contains(ids)){
                output = output.replaceAll(ids, converts.get(ids).convert(sign).toString());
            }
        }
        return output;
    }

}
