package de.polocloud.modules.serverselector.api.elements;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor @Getter
public class SignData {

    /**
     * The current signs of this session
     */
    private final List<CloudSign> cloudSigns;

}
