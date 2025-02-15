package de.polocloud.npcs.npc.entity.nms;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.npcs.npc.entity.nms.methods.NMSMethods;
import de.polocloud.npcs.npc.entity.nms.nbt.NMSNBTTag;
import de.polocloud.npcs.npc.entity.nms.wrapper.NMSClassWrapper;
import org.bukkit.entity.Entity;

public class NMSEntity {

    private final Object nms;

    public NMSEntity(Entity entity) {
        Object craftEntity = NMSClassWrapper.CRAFT_ENTITY_CLASS.getClazz().cast(entity);
        nms = NMSMethods.CRAFT_ENTITY_GET_HANDLE.invoke(craftEntity);
    }

    public void setNBTTag(NMSNBTTag nbt) {
        if(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getTemplate().getVersion().getProtocolId() >= 573){
            NMSMethods.ENTITY_LOAD_NBT.invoke(nms, nbt.asNMS());
            return;
        }
        NMSMethods.ENTITY_SET_NBT.invoke(nms, nbt.asNMS());
    }

    public void initNBTTag(NMSNBTTag nbt){
        if(PoloCloudAPI.getInstance().getGameServerManager().getThisService().getTemplate().getVersion().getProtocolId() >= 573){
            return;
        }
        NMSMethods.ENTITY_INIT_NBT.invoke(nms, nbt.asNMS());
    }

    public Entity getAsBukkitEntity() {
        Object craftEntity = NMSMethods.ENTITY_GET_BUKKIT_ENTITY.invoke(nms);
        return (Entity) craftEntity;
    }

}
