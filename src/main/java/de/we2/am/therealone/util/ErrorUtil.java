package de.we2.am.therealone.util;

import de.we2.am.therealone.web.to.error.ErrorCodeTO;
import de.we2.am.therealone.web.to.error.ErrorTO;
import de.we2.am.therealone.web.to.error.ErrorsTO;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.StringMapMessage;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class ErrorUtil {

    private ErrorUtil() {
    }

    public static ErrorsTO error(ErrorCodeTO codeTO, String message, String moreInfo, StringMapMessage log, Logger logger) {
        return error(codeTO, message, moreInfo, log, logger == null ? null : logger::info);
    }

    public static ErrorsTO error(ErrorCodeTO codeTO, String message, String moreInfo, StringMapMessage log, Consumer<Message> logger) {
        UUID trace = UUID.randomUUID();
        ErrorTO error = new ErrorTO(codeTO, message, moreInfo);
        ErrorsTO errors = new ErrorsTO(List.of(error), trace);

        if (log != null) {
            log.with(Constant.KEY_ERROR_TRACE, trace);
        }

        if (logger != null) {
            logger.accept(log);
        }

        return errors;
    }


    public static Response error(Response.Status status, ErrorCodeTO codeTO, String message, String moreInfo, StringMapMessage log, Logger logger) {
        return error(status, codeTO, message, moreInfo, log, logger == null ? null : logger::info);
    }

    public static Response error(Response.Status status, ErrorCodeTO codeTO, String message, String moreInfo, StringMapMessage log, Consumer<Message> logger) {
        ErrorsTO errors = error(codeTO, message, moreInfo, log, logger);

        return Response.status(status).entity(errors).build();
    }
}
