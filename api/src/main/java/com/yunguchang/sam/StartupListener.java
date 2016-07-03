package com.yunguchang.sam;

import javax.security.auth.message.config.AuthConfigFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Created by 禕 on 2015/9/6.
 */
@WebListener
public class StartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        AuthConfigFactory factory = AuthConfigFactory.getFactory();
        factory.registerConfigProvider(
                new FleetAuthConfigProvider(),
                "HttpServlet", getAppContextID(sce.getServletContext()),
                "Fleet manager Jaspic authentication config provider"
        );
    }


    public static String getAppContextID(ServletContext context) {
        return context.getVirtualServerName() + " " + context.getContextPath();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}