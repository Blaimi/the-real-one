package de.we2.am.therealone.web.filter;

import de.we2.am.therealone.util.Constant;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringMapMessage;

import java.io.IOException;

public class DebugLoggingResponseFilter implements ContainerResponseFilter {

    private final Logger logger = LogManager.getLogger(DebugLoggingResponseFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        StringMapMessage log = new StringMapMessage()
                .with(Constant.KEY_MESSAGE, "Handled response")
                .with(Constant.KEY_REQUEST_URI, requestContext.getUriInfo().getRequestUri())
                .with(Constant.KEY_REQUEST_METHOD, requestContext.getMethod())
                .with(Constant.KEY_RESPONSE_STATUS, responseContext.getStatus())
                .with(Constant.KEY_RESPONSE_ENTITY, responseContext.getEntity());

        logger.debug(log);
    }
}
