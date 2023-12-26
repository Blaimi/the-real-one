package de.we2.am.therealone.web.mapper;

import de.we2.am.therealone.util.Constant;
import de.we2.am.therealone.util.ErrorUtil;
import de.we2.am.therealone.web.to.error.ErrorCodeTO;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringMapMessage;

public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

    private final Logger logger = LogManager.getLogger(ForbiddenExceptionMapper.class);
    @Context
    private SecurityContext securityContext;

    @Override
    public Response toResponse(ForbiddenException exception) {
        // TODO: 18.11.23 Seperated, authentication and authorization error
        StringMapMessage log = new StringMapMessage()
                .with(Constant.KEY_MESSAGE, "No authentication / authorization")
                .with(Constant.KEY_ERROR_MESSAGE, exception.getMessage());

        if (securityContext.getUserPrincipal() != null && securityContext.getUserPrincipal().getName() != null) {
            log.with(Constant.KEY_USER_ID, securityContext.getUserPrincipal().getName());
        }

        return ErrorUtil.error(Response.Status.UNAUTHORIZED, ErrorCodeTO.NOT_AUTHORIZED, "No authentication / authorization", "Please authenticated / or get the right authorization to use this endpoint", log, logger);
    }
}
