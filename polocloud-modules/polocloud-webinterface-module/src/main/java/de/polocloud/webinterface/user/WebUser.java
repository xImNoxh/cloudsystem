package de.polocloud.webinterface.user;

import de.polocloud.api.config.IConfig;
import de.polocloud.webinterface.WebInterfaceModule;
import de.polocloud.webinterface.user.permission.WebPermission;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class WebUser implements IConfig {

    private boolean active;
    private String username;
    private byte[] passwordHash;

    private List<WebPermission.PermissionType> grantedPermissions = new ArrayList<>();

    public WebUser(String username, byte[] passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public void disableAccount(){
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void enableAccount(){
        this.active = true;
    }

    public boolean verify(byte[] salt, String password) throws InvalidKeySpecException {
        return WebInterfaceModule.getInstance().getPasswordManager().verify(password, salt, passwordHash);
    }

    public void addPermission(WebPermission.PermissionType permission) {
        grantedPermissions.add(permission);
    }

    public void save() {
        File file = new File("modules/WebInterface/users/" + this.username + ".json");

        try {
            FileWriter writer = new FileWriter(file);
            WebInterfaceModule.GSON.toJson(this, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
