package de.polocloud.webinterface.handler;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.marioslab.basis.template.Template;
import io.marioslab.basis.template.TemplateContext;

public class DashboardPageHandler extends PageHandler {
    public DashboardPageHandler() {
        super(loader.load("/dashboard/dashboard.html"));
    }

    @Override
    public void populateTemplateContext(Context context, TemplateContext templateContext) {

    }

    @Override
    public boolean isLoginPage() {
        return false;
    }
}
