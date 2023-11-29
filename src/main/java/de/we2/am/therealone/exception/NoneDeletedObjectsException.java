package de.we2.am.therealone.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.we2.am.therealone.util.Constant;
import de.we2.am.therealone.web.to.error.ErrorCodeTO;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.message.StringMapMessage;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class NoneDeletedObjectsException extends TheRealOneException {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

   private final String objectType;
   private final Object objectId;
   private final String noneDeleteObjectType;
   private final List<UUID> noneDeleteId;

   public static <T> NoneDeletedObjectsException create(String errorMessage, String moreInfo, String objectType, Object objectId, String noneDeleteObjectType, Function<T, UUID> mapper, List<T> noneDeleteId) {
       return new NoneDeletedObjectsException(errorMessage, moreInfo, objectType, objectId, noneDeleteObjectType, noneDeleteId.stream().map(mapper).toList());
   }

    public NoneDeletedObjectsException(String errorMessage, String moreInfo, String objectType, Object objectId, String noneDeleteObjectType, List<UUID> noneDeleteId) {
        super(errorMessage, Response.Status.BAD_REQUEST, ErrorCodeTO.BAD_REQUEST, moreInfo);
        this.objectType = objectType;
        this.objectId = objectId;
        this.noneDeleteObjectType = noneDeleteObjectType;
        this.noneDeleteId = noneDeleteId;
    }

    @Override
    public void log(StringMapMessage message) {
        super.log(message);
        message.with(Constant.KEY_MESSAGE, "Transitive objects are not deleted");
        message.with(Constant.KEY_OBJECT_TYPE, objectType);
        message.with(Constant.KEY_OBJECT_ID, objectId);
        message.with(Constant.KEY_ERROR_NONE_DELETE_OBJECT_TYPE, noneDeleteObjectType);
        try {
            message.with(Constant.KEY_ERROR_NONE_DELETE_OBJECT_IDS, OBJECT_MAPPER.writeValueAsString(noneDeleteId));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
