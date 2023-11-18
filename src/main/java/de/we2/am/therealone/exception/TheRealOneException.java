package de.we2.am.therealone.exception;

import de.we2.am.therealone.util.Constant;
import de.we2.am.therealone.web.to.error.ErrorCodeTO;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.message.StringMapMessage;

public class TheRealOneException extends RuntimeException {

    private final Response.Status statusCode;
    private final ErrorCodeTO errorCodeTO;
    private final String moreInfo;

    public TheRealOneException(String errorMessage, Response.Status statusCode, ErrorCodeTO errorCodeTO, String moreInfo) {
        super(errorMessage);
        this.statusCode = statusCode;
        this.errorCodeTO = errorCodeTO;
        this.moreInfo = moreInfo;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public Response.Status getStatusCode() {
        return statusCode;
    }

    public ErrorCodeTO getErrorCodeTO() {
        return errorCodeTO;
    }

    public void log(StringMapMessage message) {
        message.with(Constant.KEY_ERROR_MESSAGE, getMessage());
    }
}
