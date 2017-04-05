package com.github.mlamina;

import com.github.mlamina.resources.CommandResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

import static org.eclipse.jetty.servlets.CrossOriginFilter.*;

public class ConsoleApplication extends Application<ConsoleConfiguration> {

    public static void main(final String[] args) throws Exception {
        new ConsoleApplication().run(args);
    }

    @Override
    public String getName() {
        return "Console";
    }

    @Override
    public void initialize(final Bootstrap<ConsoleConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/"));
        bootstrap.addBundle(new AssetsBundle("/assets/css", "/css", null, "css"));
        bootstrap.addBundle(new AssetsBundle("/assets/js", "/js", null, "js"));
    }

    @Override
    public void run(final ConsoleConfiguration configuration, final Environment environment) {

        // TODO: Remove CORS filter once dev environment is set up properly
        FilterRegistration.Dynamic cors = environment.servlets()
                .addFilter("CORSFilter", CrossOriginFilter.class);

        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, environment.getApplicationContext().getContextPath() + "*");
        cors.setInitParameter(ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,HEAD,OPTIONS");
        cors.setInitParameter(ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(ALLOWED_HEADERS_PARAM, "*");
        cors.setInitParameter(EXPOSED_HEADERS_PARAM, "Link");
        cors.setInitParameter(ALLOW_CREDENTIALS_PARAM, "true");

        environment.jersey().register(new CommandResource());
    }

}
