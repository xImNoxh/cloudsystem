package de.polocloud.api.template;

import java.io.Serializable;

public enum TemplateType implements Serializable {

    PROXY,
    MINECRAFT;

    public String getDisplayName(){
        return name().toUpperCase().charAt(0) + name().substring(1).toLowerCase();
    }




}
