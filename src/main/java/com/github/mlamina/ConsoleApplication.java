package com.github.mlamina;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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
    public void run(final ConsoleConfiguration configuration,
                    final Environment environment) {
        // TODO: implement application
    }

}
