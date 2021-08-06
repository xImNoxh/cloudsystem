package de.polocloud.webinterface.handler;

import de.polocloud.webinterface.WebInterfaceModule;
import de.polocloud.webinterface.user.WebUser;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

public class PostLoginPageHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {

        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        if(username == null || password == null){
            ctx.redirect("/");
        }

        WebUser webUser = WebInterfaceModule.getInstance().getWebUserManager().getUserByUsername(username);
        boolean verify = webUser.verify(WebInterfaceModule.getInstance().getConfig().getSecurity().getSalt(), password);
        if (verify) {
            webUser.generateSessionID();
            ctx.cookie("sessionID", webUser.getSessionID());
            ctx.redirect("/dashboard");
        } else {
            ctx.redirect("/?error=NoCredentials");
        }

    }
}
