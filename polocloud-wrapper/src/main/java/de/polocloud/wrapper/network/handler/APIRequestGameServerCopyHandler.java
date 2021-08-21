package de.polocloud.wrapper.network.handler;

import de.polocloud.api.network.protocol.IPacketHandler;
import de.polocloud.api.network.protocol.packet.Packet;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerCopyPacket;
import de.polocloud.api.network.protocol.packet.api.gameserver.APIRequestGameServerCopyResponsePacket;
import de.polocloud.logger.log.Logger;
import de.polocloud.logger.log.types.LoggerType;
import de.polocloud.wrapper.Wrapper;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APIRequestGameServerCopyHandler implements IPacketHandler<Packet> {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        APIRequestGameServerCopyPacket packet = (APIRequestGameServerCopyPacket) obj;
        File tmpServerFolder = new File("tmp/" + packet.getGameservername() + "#" + packet.getSnowflakeID() + "/");
        if (!tmpServerFolder.exists()) {
            Wrapper.getInstance().getNettyClient().sendPacket(new APIRequestGameServerCopyResponsePacket(packet.getGameservername(), true, "The folder of the tmp Server doesn't exists!"));
            return;
        }

        File targetTemplateFolder = new File("templates/" + packet.getTemplate() + "/");
        if (!targetTemplateFolder.exists()) {
            Wrapper.getInstance().getNettyClient().sendPacket(new APIRequestGameServerCopyResponsePacket(packet.getGameservername(), true, "The folder of the template doesn't exists!"));
            return;
        }

        Thread copyThread = new Thread(() -> {
            if (packet.getCopyType().equals(APIRequestGameServerCopyPacket.Type.WORLD)) {
                try {
                    FileUtils.copyDirectory(new File(tmpServerFolder.getPath() + "/world/"), new File(targetTemplateFolder.getPath() + "/world/"));
                    FileUtils.copyDirectory(new File(tmpServerFolder.getPath() + "/world_nether/"), new File(targetTemplateFolder.getPath() + "/world_nether/"));
                    FileUtils.copyDirectory(new File(tmpServerFolder.getPath() + "/world_the_end/"), new File(targetTemplateFolder.getPath() + "/world_the_end/"));
                } catch (IOException e) {
                    e.printStackTrace();
                    Wrapper.getInstance().getNettyClient().sendPacket(new APIRequestGameServerCopyResponsePacket(packet.getGameservername(), true, "Unexpected error while copying files occurred!"));
                    Logger.log(LoggerType.ERROR, "Unexpected error while copying files for gameserver" + packet.getGameservername() + "!\n" +
                        "Please report this error.");
                    return;
                }
            } else {
                try {
                    List<File> d = new ArrayList<>(FileUtils.listFilesAndDirs(tmpServerFolder, TrueFileFilter.TRUE, TrueFileFilter.TRUE));
                    for (File file : d) {
                        if(!file.isDirectory()){
                            String rawPath = file.getPath().replace(tmpServerFolder.getPath(), "");
                            if (rawPath.contains("PoloCloud.json") || (!rawPath.contains("/plugins/") && rawPath.contains(".jar")) || rawPath.contains("PoloCloud-API.jar") || rawPath.contains("proxy.log.0")) {
                                continue;
                            }
                            File tar = new File(targetTemplateFolder.getPath() + rawPath);
                            tar.getParentFile().mkdirs();
                            FileUtils.copyFile(file, tar);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Wrapper.getInstance().getNettyClient().sendPacket(new APIRequestGameServerCopyResponsePacket(packet.getGameservername(), true, "Unexpected error while copying files occurred!"));
                    Logger.log(LoggerType.ERROR, "Unexpected error while copying files for gameserver" + packet.getGameservername() + "!\n" +
                        "Please report this error.");
                    return;
                }
            }
            Wrapper.getInstance().getNettyClient().sendPacket(new APIRequestGameServerCopyResponsePacket(packet.getGameservername(), false, ""));
        });
        copyThread.start();

        long started = System.currentTimeMillis();
        while (copyThread.isAlive()) {
            if ((System.currentTimeMillis() - started) > 25000) {
                Wrapper.getInstance().getNettyClient().sendPacket(new APIRequestGameServerCopyResponsePacket(packet.getGameservername(), true, "Thread timeout after 25 seconds no response"));
                Logger.log(LoggerType.ERROR, "Thread timeout after 25 seconds, while coping files for gameserver " + packet.getGameservername() + "! Stopping...");
                copyThread.stop();
            }
        }
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return APIRequestGameServerCopyPacket.class;
    }
}
