package de.we2.am.therealone.web;

import de.we2.am.therealone.web.rest.Test;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
@ApplicationPath("api/v2/assets")
public class RestConfig extends ResourceConfig {

    public RestConfig() {
        register(Test.class);
    }
}
