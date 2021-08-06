package de.polocloud.webinterface.user;

import de.polocloud.logger.log.Logger;
import de.polocloud.webinterface.WebInterfaceModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class WebUserManager {

    private List<WebUser> cachedUsers = new ArrayList<>();

    public WebUserManager(){
        cacheAllUsers();
    }

    public void cacheAllUsers(){
        File baseDir = new File("modules/WebInterface/users/");

        for (File file : baseDir.listFiles()) {
            if(file.isFile() && file.getName().endsWith(".json")){
                try {
                    WebUser webUser = WebInterfaceModule.GSON.fromJson(new FileReader(file), WebUser.class);
                    cachedUsers.add(webUser);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public WebUser loadUser(String username) {
        File file = new File("modules/WebInterface/users/" + username + ".json");

        try {
            return WebInterfaceModule.GSON.fromJson(new FileReader(file), WebUser.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public WebUser getUserBySessionID(String sessionID) {
        for (WebUser cachedUser : cachedUsers) {
            if(cachedUser.getSessionID().equals(sessionID)){
                return cachedUser;
            }
        }
        return null;
    }

    public WebUser getUserByUsername(String username) {
        for (WebUser cachedUser : cachedUsers) {
            if(cachedUser.getUsername().equalsIgnoreCase(username)){
                return cachedUser;
            }
        }
        return null;
    }
}
