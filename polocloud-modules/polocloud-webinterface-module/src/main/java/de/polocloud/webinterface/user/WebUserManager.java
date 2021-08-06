package de.polocloud.webinterface.user;

import de.polocloud.webinterface.WebInterfaceModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class WebUserManager {

    public WebUser loadUser(String username) {
        File file = new File("modules/WebInterface/users/" + username + ".json");

        try {
            return WebInterfaceModule.GSON.fromJson(new FileReader(file), WebUser.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
