package de.we2.am.therealone.exception;

import de.we2.am.therealone.util.Constant;
import de.we2.am.therealone.web.to.error.ErrorCodeTO;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.message.StringMapMessage;

public class NotFoundException extends TheRealOneException {

    private final String objectType;
    private final Object objectId;

    public NotFoundException(String errorMessage, String moreInfo, String objectType, Object objectId) {
        super(errorMessage, Response.Status.NOT_FOUND, ErrorCodeTO.BAD_REQUEST, moreInfo);
        this.objectType = objectType;
        this.objectId = objectId;
    }

    @Override
    public void log(StringMapMessage message) {
        super.log(message);
        message.with(Constant.KEY_MESSAGE, "Object with given id not found");
        message.with(Constant.KEY_OBJECT_TYPE, objectType);
        message.with(Constant.KEY_OBJECT_ID, objectId);
    }
}
