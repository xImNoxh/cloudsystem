package de.polocloud.webinterface.handler;

import io.javalin.http.Context;
import io.marioslab.basis.template.TemplateContext;

public class LoginPageHandler extends PageHandler {
    public LoginPageHandler() {
        super(loader.load("/dashboard/login.html"));
    }

    @Override
    public void populateTemplateContext(Context context, TemplateContext templateContext) {
        templateContext.set("user", "Max_DE");
    }

    @Override
    public boolean isLoginPage() {
        return true;
    }
}
