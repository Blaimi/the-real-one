package de.we2.am.therealone.web.mapper;

import de.we2.am.therealone.exception.TheRealOneException;
import de.we2.am.therealone.util.Constant;
import de.we2.am.therealone.util.ErrorUtil;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringMapMessage;

public class TheRealOneExceptionMapper implements ExceptionMapper<TheRealOneException> {

    private final Logger logger = LogManager.getLogger(TheRealOneExceptionMapper.class);

    @Override
    public Response toResponse(TheRealOneException exception) {
        // TODO: 18.11.23 Add more info from request e.g uri, user. But getting the request context result in 'The request object has been recycled and is no longer associated with this facade' on multiple request.
        StringMapMessage log = new StringMapMessage()
                .with(Constant.KEY_MESSAGE, "User request error")
                .with(Constant.KEY_ERROR_MESSAGE, exception.getMessage());

        exception.log(log);

        return ErrorUtil.error(exception.getStatusCode(), exception.getErrorCodeTO(), exception.getMessage(), exception.getMoreInfo(), log, logger);
    }
}
