package de.we2.am.therealone.web;

import de.we2.am.therealone.web.filter.CorsResponseFilter;
import de.we2.am.therealone.web.filter.DebugLoggingResponseFilter;
import de.we2.am.therealone.web.filter.JWTAuthenticationFilter;
import de.we2.am.therealone.web.mapper.ForbiddenExceptionMapper;
import de.we2.am.therealone.web.mapper.MismatchedInputExceptionMapper;
import de.we2.am.therealone.web.mapper.TheRealOneExceptionMapper;
import de.we2.am.therealone.web.resource.HealthResource;
import de.we2.am.therealone.web.resource.StatusResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfig extends ResourceConfig {

    public RestConfig() {
        // Register filters
        register(DebugLoggingResponseFilter.class);
        register(CorsResponseFilter.class);
        register(JWTAuthenticationFilter.class);

        // Register exception mapper
        register(ForbiddenExceptionMapper.class);
        register(MismatchedInputExceptionMapper.class);
        register(TheRealOneExceptionMapper.class);

        // Register resources
        register(HealthResource.class);
        register(StatusResource.class);
    }
}
