package de.polocloud.npcs.npc.entity;

import org.bukkit.entity.EntityType;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityProvider {

    public static EntityType getEntityTypeByName(String s){
        if(s.equalsIgnoreCase("player")){
            return null;
        }
        return EntityType.valueOf(s.toUpperCase());
    }

    public static List<EntityType> getAvailableTypes(){
        List<EntityType> available = new LinkedList<>();
        for (EntityType value : EntityType.values()) {
            if(value.isAlive() && value.isSpawnable()){
                if(!value.equals(EntityType.PLAYER)){
                    available.add(value);
                }
            }
        }
        return available;
    }

    public static List<String> getAvailableStringTypes(){
        return getAvailableTypes().stream().map(type -> type.getName().toLowerCase()).collect(Collectors.toList());
    }

}
