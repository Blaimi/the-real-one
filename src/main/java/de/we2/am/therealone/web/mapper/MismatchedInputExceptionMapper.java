package de.we2.am.therealone.web.mapper;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import de.we2.am.therealone.util.Constant;
import de.we2.am.therealone.util.ErrorUtil;
import de.we2.am.therealone.web.to.error.ErrorCodeTO;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringMapMessage;

public class MismatchedInputExceptionMapper implements ExceptionMapper<MismatchedInputException> {

    private final Logger logger = LogManager.getLogger(MismatchedInputExceptionMapper.class);

    @Override
    public Response toResponse(MismatchedInputException exception) {
        StringMapMessage log = new StringMapMessage()
                .with(Constant.KEY_MESSAGE, "Error while parsing request")
                .with(Constant.KEY_ERROR_MESSAGE, exception.getMessage());

        return ErrorUtil.error(Response.Status.BAD_REQUEST, ErrorCodeTO.BAD_REQUEST, "Error while parsing request", exception.getOriginalMessage(), log, logger);
    }
}
