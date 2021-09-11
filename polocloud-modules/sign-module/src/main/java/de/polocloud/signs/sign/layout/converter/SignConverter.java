package de.polocloud.signs.sign.layout.converter;

import com.google.common.collect.Maps;
import de.polocloud.signs.sign.base.IGameServerSign;

import java.util.Map;

public class SignConverter {

    /**
     * List for containing all available placeholders
     */
    private static final Map<String, ConvertStep> CONVERTERS = Maps.newConcurrentMap();


    static {
        CONVERTERS.put("%SERVICE%", signService -> signService.getGameServer() == null ? "N/A" : signService.getGameServer().getName());
        CONVERTERS.put("%MAX_PLAYERS%", signService ->  signService.getGameServer() == null ? "N/A" : signService.getGameServer().getMaxPlayers());
        CONVERTERS.put("%ONLINE_PLAYERS%", signService ->  signService.getGameServer() == null ? "N/A" : signService.getGameServer().getOnlinePlayers());
        CONVERTERS.put("%MOTD%", signService ->  signService.getGameServer() == null ? "N/A" :  signService.getGameServer().getMotd());
        CONVERTERS.put("%GROUP%", signService ->  signService.getGameServer() == null ? "N/A" : signService.getTemplate().getName());
        CONVERTERS.put("%TEMPLATE%", signService ->  signService.getTemplate().getName());
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
        for (String ids : CONVERTERS.keySet()) {
            if(output.contains(ids)){
                output = output.replaceAll(ids, CONVERTERS.get(ids).convert(sign).toString());
            }
        }
        return output;
    }

}
