package de.polocloud.bootstrap.network.handler.wrapper;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.module.ModuleCopyType;
import de.polocloud.api.network.packets.master.MasterLoginResponsePacket;
import de.polocloud.api.network.packets.wrapper.WrapperLoginPacket;
import de.polocloud.api.network.packets.wrapper.WrapperTransferModulesPacket;
import de.polocloud.api.wrapper.base.IWrapper;
import de.polocloud.api.wrapper.IWrapperManager;
import de.polocloud.bootstrap.Master;
import de.polocloud.bootstrap.wrapper.SimpleMasterWrapper;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.console.ConsoleColors;
import de.polocloud.api.logger.helper.LogLevel;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.function.BiConsumer;

public abstract class WrapperHandlerServiceController {

    protected final IWrapperManager wrapperManager;

    protected WrapperHandlerServiceController() {
        this.wrapperManager = PoloCloudAPI.getInstance().getWrapperManager();
    }


    public MasterLoginResponsePacket getMasterLoginResponsePacket(boolean response) {
        return new MasterLoginResponsePacket(response, response ?
            "Master authentication §asuccessfully §7completed."
            : "Master authentication §cdenied§7!");
    }

    public void getLoginResponse(WrapperLoginPacket packet, BiConsumer<Boolean, IWrapper> response, ChannelHandlerContext ctx) {
        IWrapper wrapper = packet.getWrapper();

        SimpleMasterWrapper simpleMasterWrapper = new SimpleMasterWrapper(wrapper.getName(), ctx, wrapper.getMaxMemory(), wrapper.getMaxSimultaneouslyStartingServices());
        simpleMasterWrapper.setAuthenticated(wrapper.isAuthenticated());

        response.accept(PoloCloudAPI.getInstance().getMasterConfig().getProperties().getWrapperKey().equals(packet.getKey()), simpleMasterWrapper);
    }

    public void sendWrapperSuccessfully(IWrapper wrapper, WrapperLoginPacket packet) {
        PoloLogger.print(LogLevel.INFO, "The Wrapper " + ConsoleColors.LIGHT_BLUE + packet.getWrapper().getName() + ConsoleColors.GRAY + " is successfully connected to the master.");
        LinkedHashMap<File, ModuleCopyType[]> modulesWithInfo = Master.getInstance().getModuleService().getAllModulesToCopyWithInfo();
        if (!modulesWithInfo.isEmpty()) {
            wrapper.sendPacket(new WrapperTransferModulesPacket(modulesWithInfo));
        }
    }

}
