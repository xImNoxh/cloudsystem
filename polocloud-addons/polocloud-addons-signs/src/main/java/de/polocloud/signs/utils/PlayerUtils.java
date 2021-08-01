package de.polocloud.signs.utils;

import de.polocloud.api.gameserver.IGameServer;
import de.polocloud.signs.bootstrap.SignBootstrap;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.Sign;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerUtils {

    public static void sendService(IGameServer gameServer, Player player) {
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

    public static Block getBlockSignAttachedTo(Block block) {
        Sign s = (Sign) block.getState().getData();
        Block attachedBlock = block.getRelative(s.getAttachedFace());
        return attachedBlock;
    }

}
