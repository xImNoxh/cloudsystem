package de.polocloud.tablist.attribute;

import com.google.common.collect.Maps;

import java.util.Map;

public class AttributeConverter {

    private Map<AttributeKeys, Attribute> attributes;

    public AttributeConverter() {
        attributes = Maps.newConcurrentMap();

        attributes.put(AttributeKeys.ONLINE_COUNT, new Attribute() {
            @Override
            public Object execute() {
                return -1;
            }
        });


    }
}
