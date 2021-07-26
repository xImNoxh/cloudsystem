package de.polocloud.signs.utils;

import org.bukkit.entity.Player;
import de.polocloud.signs.bootstrap.Bootstrap;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class PlayerUtils {

    public static void sendPlayerToServer(Player player, String server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(Bootstrap.getInstance(), "BungeeCord", b.toByteArray());
            b.close();
            out.close();
        } catch (Exception e) {

        }
    }

}
