package de.polocloud.wrapper.impl.handler;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.gameserver.GameServerCopyPacket;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.template.ITemplateManager;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WrapperHandlerCopyServer implements IPacketHandler<Packet> {

    @Override
    public void handlePacket(ChannelHandlerContext ctx, Packet obj) {
        GameServerCopyPacket packet = (GameServerCopyPacket) obj;
        IGameServer gameServer = packet.getGameServer();

        if (gameServer == null) {
            PoloCloudAPI.getInstance().messageCloud("§cTried copying a §enulled GameServer §cinto its template!");
            return;
        }

        File tmpServerFolder = new File(FileConstants.WRAPPER_DYNAMIC_SERVERS, gameServer.getName() + "#" + gameServer.getSnowflake() + "/");
        if (!tmpServerFolder.exists()) {
            PoloCloudAPI.getInstance().messageCloud("§cCouldn't copy §e" + gameServer.getName() + " §cinto its template! The folder of the tmp Server doesn't exists!");
            return;
        }

        File targetTemplateFolder = new File(FileConstants.WRAPPER_TEMPLATES,gameServer.getTemplate().getName() + "/");
        if (!targetTemplateFolder.exists()) {
            PoloCloudAPI.getInstance().messageCloud("§cCouldn't copy §e" + gameServer.getName() + " §cinto its template! The folder of the template doesn't exists!");
            return;
        }

        Thread copyThread = new Thread(() -> {
            if (packet.getCopyType().equals(ITemplateManager.Type.WORLD)) {
                try {
                    FileUtils.copyDirectory(new File(tmpServerFolder.getPath() + "/world/"), new File(targetTemplateFolder.getPath() + "/world/"));
                    FileUtils.copyDirectory(new File(tmpServerFolder.getPath() + "/world_nether/"), new File(targetTemplateFolder.getPath() + "/world_nether/"));
                    FileUtils.copyDirectory(new File(tmpServerFolder.getPath() + "/world_the_end/"), new File(targetTemplateFolder.getPath() + "/world_the_end/"));
                } catch (IOException e) {
                    PoloCloudAPI.getInstance().messageCloud("§cCouldn't copy §e" + gameServer.getName() + " §cinto its template! An Unexpected error while copying files occurred! Have a look at the Wrapper-Console!");
                    PoloLogger.print(LogLevel.ERROR, "Unexpected error while copying files for gameserver" + gameServer.getName() + "!\n" + "Please report this error.");
                    PoloCloudAPI.getInstance().reportException(e);
                    return;
                }
            } else {
                try {
                    List<File> queuedToCopy = new ArrayList<>(FileUtils.listFilesAndDirs(tmpServerFolder, TrueFileFilter.TRUE, TrueFileFilter.TRUE));
                    for (File file : queuedToCopy) {
                        if(!file.isDirectory()){
                            String rawPath = file.getPath().replace(tmpServerFolder.getPath(), "");
                            if (rawPath.contains(FileConstants.CLOUD_JSON_NAME) || (!rawPath.contains("/plugins/") && rawPath.contains(".jar")) || rawPath.contains(FileConstants.CLOUD_API_NAME) || rawPath.contains("proxy.log.0") || rawPath.contains("latest.log")) {
                                continue;
                            }
                            File tar = new File(targetTemplateFolder.getPath() + rawPath);
                            tar.getParentFile().mkdirs();
                            FileUtils.copyFile(file, tar);
                        }
                    }
                } catch (IOException e) {
                    PoloCloudAPI.getInstance().messageCloud("§cCouldn't copy §e" + gameServer.getName() + " §cinto its template! Unexpected error while copying files occurred!");
                    PoloLogger.print(LogLevel.ERROR, "Unexpected error while copying files for gameserver" + gameServer.getName() + "!\n" + "Please report this error.");
                    PoloCloudAPI.getInstance().reportException(e);
                    return;
                }
            }
            PoloCloudAPI.getInstance().messageCloud("§aSuccessfully §7copied §b" + gameServer.getName() + " §7into its template!");
        });
        copyThread.start();

        long started = System.currentTimeMillis();
        while (copyThread.isAlive()) {
            if ((System.currentTimeMillis() - started) > 25000) {
                PoloCloudAPI.getInstance().messageCloud("§cCouldn't copy §e" + gameServer.getName() + " §cinto its template! Thread timeout after 25 seconds no response");
                PoloLogger.print(LogLevel.ERROR, "Thread timeout after 25 seconds, while coping files for gameserver " + gameServer.getName() + "! Stopping...");
                copyThread.interrupt();
            }
        }
    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return GameServerCopyPacket.class;
    }
}
