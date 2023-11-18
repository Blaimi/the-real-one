package de.we2.am.therealone.web;

import de.we2.am.therealone.web.resource.StatusResource;
import de.we2.am.therealone.web.rest.Test;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfig extends ResourceConfig {

    public RestConfig() {
        register(StatusResource.class);
        register(Test.class);
    }
}
