package de.we2.am.therealone.exception;

import de.we2.am.therealone.util.Constant;
import de.we2.am.therealone.web.to.error.ErrorCodeTO;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.message.StringMapMessage;

public class InvalidArgumentException extends TheRealOneException {

    private final String objectType;
    private final String argumentName;
    private final String argument;
    private final String argumentPattern;

    public InvalidArgumentException(String errorMessage, String moreInfo, String objectType, String argumentName, String argument, String argumentPattern) {
        super(errorMessage, Response.Status.BAD_REQUEST, ErrorCodeTO.BAD_REQUEST, moreInfo);
        this.objectType = objectType;
        this.argumentName = argumentName;
        this.argument = argument;
        this.argumentPattern = argumentPattern;
    }

    @Override
    public void log(StringMapMessage message) {
        super.log(message);
        message.with(Constant.KEY_MESSAGE, "Argument is not in the right format");
        message.with(Constant.KEY_OBJECT_TYPE, objectType);
        message.with(Constant.KEY_OBJECT_ARGUMENT_NAME, argumentName);
        message.with(Constant.KEY_OBJECT_ARGUMENT_VALUE, argument);
        if (argumentPattern != null) {
            message.with(Constant.KEY_OBJECT_ARGUMENT_PATTERN, argumentPattern);
        }
    }
}
