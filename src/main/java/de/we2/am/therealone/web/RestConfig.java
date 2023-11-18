package de.we2.am.therealone.web;

import de.we2.am.therealone.web.resource.HealthResource;
import de.we2.am.therealone.web.resource.StatusResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfig extends ResourceConfig {

    public RestConfig() {
        // Register resources
        register(HealthResource.class);
        register(StatusResource.class);
    }
}
