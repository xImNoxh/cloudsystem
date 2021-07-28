package de.polocloud.signs.utils;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.signs.bootstrap.SignBootstrap;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerUtils {

    public static void sendService(IGameServer gameServer, Player player){

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(gameServer.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(SignBootstrap.getInstance(), "BungeeCord", b.toByteArray());
    }

}
