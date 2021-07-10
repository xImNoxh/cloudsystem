package de.polocloud.api.template;

public enum TemplateType {

    PROXY,
    MINECRAFT;

    public String getDisplayName(){
        return name().toUpperCase().charAt(0) + name().substring(1).toLowerCase();
    }




}
