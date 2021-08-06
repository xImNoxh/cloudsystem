package de.polocloud.webinterface;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.polocloud.api.CloudAPI;
import de.polocloud.api.module.Module;
import de.polocloud.webinterface.command.WebInterfaceCommand;
import de.polocloud.webinterface.config.WebInterfaceConfig;
import de.polocloud.webinterface.handler.DashboardPageHandler;
import de.polocloud.webinterface.handler.LoginPageHandler;
import de.polocloud.webinterface.handler.PostLoginPageHandler;
import de.polocloud.webinterface.security.PasswordManager;
import de.polocloud.webinterface.user.WebUserManager;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

import java.io.File;

public class WebInterfaceModule extends Module {

    private WebInterfaceConfig config;
    private Javalin javalin;

    private PasswordManager passwordManager;
    private WebUserManager webUserManager;

    private static WebInterfaceModule instance;

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void onLoad() {
        instance = this;
        this.loadConfig();

        this.passwordManager = new PasswordManager();
        this.webUserManager = new WebUserManager();

        //change context ClassLoader to prevent Dependency-Collision
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(WebInterfaceModule.class.getClassLoader());

        this.javalin = Javalin.create(config -> {
            config.addStaticFiles("dashboard/");
        });

        this.javalin.get("/", new LoginPageHandler());
        this.javalin.get("/dashboard", new DashboardPageHandler());

        this.javalin.post("/post/login", new PostLoginPageHandler());

        this.javalin.start(config.getNetwork().getPort());

        //change back to original ContextClassLoader
        Thread.currentThread().setContextClassLoader(classLoader);

        CloudAPI.getInstance().getCommandPool().registerCommand(new WebInterfaceCommand(config));
    }

    private void loadConfig() {
        File configPath = new File("modules/WebInterface/");
        if (!configPath.exists()) {
            configPath.mkdirs();
        }
        configPath = new File(configPath + "/users/");
        if (!configPath.exists()) {
            configPath.mkdirs();
        }

        File configFile = new File("modules/WebInterface/config.json");
        this.config = getConfigLoader().load(WebInterfaceConfig.class, configFile);
        if (this.config.getSecurity().getSalt() == null) {
            this.config.getSecurity().generateNewSalt();
        }


        getConfigSaver().save(this.config, configFile);

    }

    @Override
    public boolean onReload() {
        return true;
    }

    @Override
    public void onShutdown() {
        this.javalin.stop();
    }

    public PasswordManager getPasswordManager() {
        return passwordManager;
    }

    public WebInterfaceConfig getConfig() {
        return config;
    }

    public WebUserManager getWebUserManager() {
        return webUserManager;
    }

    public static WebInterfaceModule getInstance() {
        return instance;
    }
}
