package de.polocloud.wrapper.impl.handler;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.config.FileConstants;
import de.polocloud.api.gameserver.base.IGameServer;
import de.polocloud.api.network.protocol.packet.handler.IPacketHandler;
import de.polocloud.api.network.protocol.packet.base.Packet;
import de.polocloud.api.network.packets.gameserver.GameServerCopyPacket;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.scheduler.Scheduler;
import de.polocloud.api.template.ITemplateManager;
import de.polocloud.api.template.helper.TemplateType;
import de.polocloud.api.util.PoloHelper;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
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
            targetTemplateFolder.mkdirs();
        }

        Scheduler.runtimeScheduler().async().schedule(() -> {
            if (packet.getCopyType().equals(ITemplateManager.Type.WORLD)) {

                //Overworld
                File worldFolder = new File(tmpServerFolder, "world/");
                File templateWorldFolder = new File(targetTemplateFolder, "world/");

                //Nether
                File netherFolder = new File(tmpServerFolder, "world_nether/");
                File templateNetherFolder = new File(targetTemplateFolder, "world_nether/");

                //End
                File endFolder = new File(tmpServerFolder, "world_the_end/");
                File templateEndFolder = new File(targetTemplateFolder, "world_the_end/");

                if (worldFolder.exists()) {
                    try {
                        PoloHelper.copyFolder(worldFolder, templateWorldFolder);
                        //FileUtils.copyDirectory(worldFolder, templateWorldFolder);


                        // World copying was successful
                        // -> Copying Nether now

                        if (netherFolder.exists()) {
                            try {
                                PoloHelper.copyFolder(netherFolder, templateNetherFolder);
                                //FileUtils.copyDirectory(netherFolder, templateNetherFolder);


                                // Nether copying was successful
                                // -> Copying End now

                                if (endFolder.exists()) {
                                    try {
                                        PoloHelper.copyFolder(endFolder, templateEndFolder);
                                        //FileUtils.copyDirectory(endFolder, templateEndFolder);
                                    } catch (Exception e) {
                                        if (e instanceof FileSystemException) {
                                            return;
                                        }
                                        PoloCloudAPI.getInstance().messageCloud("§cCouldn't copy §eworld_the_end §cof server §e" + gameServer.getName() + " §cinto its template! An Unexpected error while copying files occurred! Have a look at the Wrapper-Console!");
                                        PoloLogger.print(LogLevel.ERROR, "Unexpected error while copying files for gameserver" + gameServer.getName() + "!\n" + "Please report this error.");
                                        e.printStackTrace();
                                    }
                                }


                            } catch (Exception e) {
                                if (e instanceof FileSystemException) {
                                    return;
                                }
                                PoloCloudAPI.getInstance().messageCloud("§cCouldn't copy §eworld_nether §cof server §e" + gameServer.getName() + " §cinto its template! An Unexpected error while copying files occurred! Have a look at the Wrapper-Console!");
                                PoloLogger.print(LogLevel.ERROR, "Unexpected error while copying files for gameserver" + gameServer.getName() + "!\n" + "Please report this error.");
                                e.printStackTrace();
                            }
                        }

                    } catch (Exception e) {
                        if (e instanceof FileSystemException) {
                            return;
                        }
                        PoloCloudAPI.getInstance().messageCloud("§cCouldn't copy §eworld §cof server §e" + gameServer.getName() + " §cinto its template! An Unexpected error while copying files occurred! Have a look at the Wrapper-Console!");
                        PoloLogger.print(LogLevel.ERROR, "Unexpected error while copying files for gameserver" + gameServer.getName() + "!\n" + "Please report this error.");
                        e.printStackTrace();
                    }
                }

            } else {


                try {
                    PoloHelper.copyFolder(
                        tmpServerFolder,
                        targetTemplateFolder,
                        FileConstants.CLOUD_API_NAME,
                        FileConstants.CLOUD_JSON_NAME,
                        "latest.log",
                        "proxy.log.0",
                        "proxy.jar",
                        "spigot.jar"
                    );

                } catch (Exception e) {
                    PoloCloudAPI.getInstance().messageCloud("§cCouldn't copy §e" + gameServer.getName() + " §cinto its template! Unexpected error while copying files occurred!");
                    PoloLogger.print(LogLevel.ERROR, "Unexpected error while copying files for gameserver" + gameServer.getName() + "!\n" + "Please report this error.");
                    PoloCloudAPI.getInstance().reportException(e);
                    return;
                }

            }
            PoloCloudAPI.getInstance().messageCloud("§aSuccessfully §7copied §b" + gameServer.getName() + " §7into its template!");

        });

    }

    @Override
    public Class<? extends Packet> getPacketClass() {
        return GameServerCopyPacket.class;
    }
}
