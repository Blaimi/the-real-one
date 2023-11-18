package de.we2.am.therealone.web.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;

import java.io.IOException;
import java.util.function.Predicate;
import java.util.regex.Pattern;

// Adapted from https://gitlab.com/biletado/backend/go/-/blob/main/src/cors/addHeaders.go?ref_type=heads
public class CorsResponseFilter implements ContainerResponseFilter {

    private static final Predicate<String> ORIGIN_TEST = Pattern.compile("^http://localhost(:[0-9]+)?$").asMatchPredicate();

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String origin = responseContext.getHeaderString("Origin");
        if (origin != null && ORIGIN_TEST.test(origin)) {
            responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
            responseContext.getHeaders().add("Vary", "Origin");
        }

        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Authorization");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
    }
}
